package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticacion", description = "Inicio de sesion tradicional y Google con JWT")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @Value("${app.google.client-id:}")
    private String googleClientId;

    public AuthController(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder encoder,
                          JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    @Operation(summary = "Autenticar usuario tradicional y devolver token JWT")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = request.getCorreo() == null ? null
                : usuarioRepository.findByCorreoAndActivoTrue(request.getCorreo()).orElse(null);

        if (usuario == null || request.getPassword() == null || !passwordCorrecta(usuario, request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
        return ResponseEntity.ok(respuestaLogin(usuario));
    }

    @PostMapping("/google")
    @Operation(summary = "Autenticar o registrar usuario con Google (token verificado) y devolver JWT")
    public ResponseEntity<?> loginGoogle(@RequestBody Map<String, String> body) {
        JsonNode g = verificarGoogle(body.get("credential"));
        if (g == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token de Google inválido"));
        }
        String correo = g.path("email").asText();
        String nombre = g.hasNonNull("given_name") ? g.get("given_name").asText() : "Usuario";
        String apellidos = g.hasNonNull("family_name") ? g.get("family_name").asText() : "Google";

        Usuario usuario = usuarioRepository.findByCorreoAndActivoTrue(correo).orElse(null);
        if (usuario == null) {
            Rol rolCliente = rolRepository.findByNombreRol("CLIENTE")
                    .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
            String runTemp = "G" + String.format("%08d", Math.abs(UUID.randomUUID().hashCode() % 100000000));
            usuario = usuarioRepository.save(Usuario.builder()
                    .correo(correo)
                    .nombre(nombre.length() > 50 ? nombre.substring(0, 50) : nombre)
                    .apellidos(apellidos.length() > 100 ? apellidos.substring(0, 100) : apellidos)
                    .run(runTemp)
                    .password(encoder.encode(UUID.randomUUID().toString()))
                    .rol(rolCliente)
                    .activo(true)
                    .build());
        }
        return ResponseEntity.ok(respuestaLogin(usuario));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token JWT vigente (verifica que el usuario siga activo)")
    public ResponseEntity<?> renovar(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token no proporcionado en la cabecera Authorization"));
        }
        try {
            Claims c = jwtUtil.leer(authHeader.substring(7));
            Usuario u = usuarioRepository.findById(c.get("id", Integer.class))
                    .filter(x -> Boolean.TRUE.equals(x.getActivo()))
                    .orElseThrow();
            return ResponseEntity.ok(Map.of("token", jwtUtil.generar(u)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token invalido o expirado"));
        }
    }

    /** Valida el ID token con Google y comprueba que fue emitido para NUESTRA app. */
    private JsonNode verificarGoogle(String credential) {
        if (googleClientId.isBlank() || credential == null || credential.isBlank()) return null;
        try {
            JsonNode n = RestClient.create().get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={t}", credential)
                    .retrieve().body(JsonNode.class);
            boolean ok = n != null
                    && googleClientId.equals(n.path("aud").asText())
                    && "true".equals(n.path("email_verified").asText());
            return ok ? n : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> respuestaLogin(Usuario usuario) {
        LoginResponse datos = LoginResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreo())
                .idRol(usuario.getRol().getIdRol())
                .nombreRol(usuario.getRol().getNombreRol())
                .build();
        return Map.of("token", jwtUtil.generar(usuario), "usuario", datos);
    }

    /** Acepta BCrypt y, si la clave aun esta en texto plano (usuarios antiguos), la convierte al primer login. */
    private boolean passwordCorrecta(Usuario u, String plano) {
        String guardada = u.getPassword();
        if (guardada.startsWith("$2")) return encoder.matches(plano, guardada);
        if (!guardada.equals(plano)) return false;
        u.setPassword(encoder.encode(plano));
        usuarioRepository.save(u);
        return true;
    }
}
package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestion de cuentas de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios activos (solo ADMIN)")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @Operation(summary = "Obtener usuario por ID (admin o el propio usuario)")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar usuario (publico = CLIENTE; ADMIN puede elegir rol)")
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario, Authentication auth) {
        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(usuario, admin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    @Operation(summary = "Actualizar usuario (admin o el propio usuario)")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id, @RequestBody Usuario usuario,
                                              Authentication auth) {
        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(usuarioService.actualizar(id, usuario, admin));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar usuario (solo ADMIN; no puede eliminarse a si mismo)")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id, Authentication auth) {
        if (auth != null && id.equals(auth.getPrincipal())) {
            throw new RuntimeException("No puedes eliminar tu propia cuenta.");
        }
        usuarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}

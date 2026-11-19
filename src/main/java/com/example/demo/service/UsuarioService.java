package com.example.demo.service;

import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.encoder = encoder;
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findByActivoTrue();
    }

    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /** Registro. Solo un admin puede elegir el rol; el registro público siempre crea un CLIENTE. */
    @Transactional
    public Usuario crear(Usuario u, boolean puedeElegirRol) {
        Rol rol;
        if (puedeElegirRol && u.getRol() != null && u.getRol().getIdRol() != null) {
            rol = rolRepository.findById(u.getRol().getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        } else {
            rol = rolRepository.findByNombreRol("CLIENTE")
                    .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
        }
        u.setIdUsuario(null);
        u.setRol(rol);
        u.setActivo(true);
        u.setPassword(encoder.encode(validarPassword(u.getPassword())));
        return usuarioRepository.save(u);
    }

    @Transactional
    public Usuario actualizar(Integer id, Usuario d, boolean esAdmin) {
        Usuario u = obtenerPorId(id);
        if (d.getRun() != null) u.setRun(d.getRun());
        if (d.getNombre() != null) u.setNombre(d.getNombre());
        if (d.getApellidos() != null) u.setApellidos(d.getApellidos());
        if (d.getCorreo() != null) u.setCorreo(d.getCorreo());
        u.setTelefono(d.getTelefono());
        u.setDireccion(d.getDireccion());
        if (d.getRegion() != null) u.setRegion(d.getRegion());
        if (d.getComuna() != null) u.setComuna(d.getComuna());
        if (d.getPassword() != null && !d.getPassword().isBlank()) {
            u.setPassword(encoder.encode(validarPassword(d.getPassword())));
        }
        if (esAdmin && d.getRol() != null && d.getRol().getIdRol() != null) {
            u.setRol(rolRepository.findById(d.getRol().getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        }
        return usuarioRepository.save(u);
    }

    @Transactional
    public void desactivar(Integer id) {
        Usuario u = obtenerPorId(id);
        u.setActivo(false);
        usuarioRepository.save(u);
    }

    private String validarPassword(String p) {
        if (p == null || p.length() < 4 || p.length() > 10) {
            throw new RuntimeException("La contraseña debe tener entre 4 y 10 caracteres.");
        }
        return p;
    }
}

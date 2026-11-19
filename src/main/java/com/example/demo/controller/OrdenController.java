package com.example.demo.controller;

import com.example.demo.dto.OrdenRequest;
import com.example.demo.model.Orden;
import com.example.demo.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordenes")
@Tag(name = "Ordenes", description = "Gestion de pedidos y compras")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    private static boolean tieneRol(Authentication a, String rol) {
        return a != null && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals(rol));
    }
    private static boolean esStaff(Authentication a) {
        return tieneRol(a, "ROLE_ADMIN") || tieneRol(a, "ROLE_VENDEDOR");
    }
    private static Integer idActual(Authentication a) { return a != null ? (Integer) a.getPrincipal() : null; }

    @GetMapping
    @Operation(summary = "Listar ordenes. Staff: todas, por usuario o por rango de fechas. Cliente: solo las suyas")
    public ResponseEntity<List<Orden>> listar(
            @RequestParam(name = "id_usuario", required = false) Integer idUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Authentication auth) {
        if (!esStaff(auth)) {
            return ResponseEntity.ok(ordenService.obtenerPorUsuario(idActual(auth)));
        }
        if (desde != null || hasta != null) {
            return ResponseEntity.ok(ordenService.obtenerPorFechas(desde, hasta));
        }
        return ResponseEntity.ok(idUsuario != null
                ? ordenService.obtenerPorUsuario(idUsuario) : ordenService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID (staff o dueño)")
    public ResponseEntity<Orden> obtenerPorId(@PathVariable Integer id, Authentication auth) {
        Orden orden = ordenService.obtenerPorId(id);
        boolean duenio = orden.getUsuario() != null && orden.getUsuario().getIdUsuario().equals(idActual(auth));
        if (!esStaff(auth) && !duenio) throw new AccessDeniedException("No tienes acceso a esta orden.");
        return ResponseEntity.ok(orden);
    }

    @PostMapping
    @Operation(summary = "Crear orden de compra (precios y total se calculan en el servidor)")
    public ResponseEntity<Orden> crear(@RequestBody OrdenRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(request, idActual(auth)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cambiar el estado de una orden")
    public ResponseEntity<Orden> cambiarEstado(@PathVariable Integer id, @RequestBody Map<String, String> body,
                                               Authentication auth) {
        return ResponseEntity.ok(ordenService.cambiarEstado(id, body.get("estado"),
                idActual(auth), tieneRol(auth, "ROLE_ADMIN")));
    }
}

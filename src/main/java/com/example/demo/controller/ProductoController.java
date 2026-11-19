package com.example.demo.controller;

import com.example.demo.model.Producto;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Gestion del catalogo de gemas y productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar productos activos (?oferta=1 solo ofertas)")
    public ResponseEntity<List<Producto>> listar(@RequestParam(required = false) Integer oferta) {
        return ResponseEntity.ok(oferta != null && oferta == 1
                ? productoService.obtenerEnOferta() : productoService.obtenerTodos());
    }

    @GetMapping("/ofertas")
    @Operation(summary = "Listar productos en oferta")
    public ResponseEntity<List<Producto>> listarOfertas() {
        return ResponseEntity.ok(productoService.obtenerEnOferta());
    }

    @GetMapping("/criticos")
    @Operation(summary = "Productos con stock critico (solo ADMIN)")
    public ResponseEntity<List<Producto>> listarCriticos() {
        return ResponseEntity.ok(productoService.obtenerCriticos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear producto (solo ADMIN)")
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(producto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto (solo ADMIN)")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id, @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizar(id, producto));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Actualizar el stock de un producto (solo ADMIN)")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Integer id, @RequestBody Map<String, Integer> payload) {
        return ResponseEntity.ok(productoService.actualizarStock(id, payload.get("stock")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminacion logica de un producto (solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.demo.controller;

import com.example.demo.model.Contacto;
import com.example.demo.service.ContactoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacto")
@Tag(name = "Contacto", description = "Recepcion y lectura de mensajes de clientes")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    @Operation(summary = "Listar mensajes de contacto (solo ADMIN)")
    public ResponseEntity<List<Contacto>> listar() {
        return ResponseEntity.ok(contactoService.obtenerTodos());
    }

    @PostMapping
    @Operation(summary = "Enviar mensaje de contacto (publico)")
    public ResponseEntity<Contacto> crear(@RequestBody Contacto contacto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactoService.guardar(contacto));
    }

    @PutMapping("/{id}/leido")
    @Operation(summary = "Marcar mensaje como leido (solo ADMIN)")
    public ResponseEntity<Contacto> marcarLeido(@PathVariable Integer id) {
        return ResponseEntity.ok(contactoService.marcarLeido(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mensaje (solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        contactoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

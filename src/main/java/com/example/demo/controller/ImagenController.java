package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/imagenes")
public class ImagenController {

    // Solo estas carpetas y extensiones están permitidas
    private static final List<String> TIPOS = List.of("productos", "blogs");
    private static final List<String> EXTENSIONES = List.of("jpg", "jpeg", "png", "webp");

    private final Path carpetaBase;

    // La carpeta se define en application.properties (por defecto "uploads")
    public ImagenController(@Value("${app.uploads.dir:uploads}") String carpeta) {
        this.carpetaBase = Paths.get(carpeta).toAbsolutePath().normalize();
    }

    @PostMapping("/{tipo}")
    public ResponseEntity<Map<String, String>> subir(@PathVariable String tipo,
                                                     @RequestParam("archivo") MultipartFile archivo) throws IOException {
        if (!TIPOS.contains(tipo)) {
            throw new RuntimeException("Tipo de imagen no válido.");
        }
        if (archivo.isEmpty()) {
            throw new RuntimeException("Debes seleccionar una imagen.");
        }

        // Valida que sea una imagen y que la extensión esté permitida
        String contentType = archivo.getContentType();
        String original = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename();
        String extension = original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1).toLowerCase()
                : "";
        if (contentType == null || !contentType.startsWith("image/") || !EXTENSIONES.contains(extension)) {
            throw new RuntimeException("Solo se permiten imágenes JPG, PNG o WEBP.");
        }

        // Nombre aleatorio: nunca se usa el nombre que mandó el usuario
        String nombreNuevo = UUID.randomUUID() + "." + extension;

        Path carpeta = carpetaBase.resolve(tipo);
        Files.createDirectories(carpeta);
        Files.copy(archivo.getInputStream(), carpeta.resolve(nombreNuevo));

        return ResponseEntity.ok(Map.of("imagen", "/uploads/" + tipo + "/" + nombreNuevo));
    }
}
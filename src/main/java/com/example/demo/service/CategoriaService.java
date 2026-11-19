package com.example.demo.service;

import com.example.demo.model.Categoria;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Categoria> obtenerTodas() { return categoriaRepository.findByActivoTrue(); }

    public Categoria obtenerPorId(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    @Transactional
    public Categoria crear(Categoria c) {
        String nombre = validarNombre(c.getNombreCategoria());
        var existente = categoriaRepository.findByNombreCategoriaIgnoreCase(nombre);
        if (existente.isPresent()) {
            Categoria e = existente.get();
            if (Boolean.TRUE.equals(e.getActivo())) {
                throw new RuntimeException("Ya existe una categoría con ese nombre.");
            }
            e.setActivo(true);                       // reactiva una categoría eliminada antes
            if (c.getDescripcion() != null) e.setDescripcion(c.getDescripcion());
            return categoriaRepository.save(e);
        }
        c.setIdCategoria(null);
        c.setNombreCategoria(nombre);
        c.setActivo(true);
        return categoriaRepository.save(c);
    }

    @Transactional
    public Categoria actualizar(Integer id, Categoria d) {
        Categoria c = obtenerPorId(id);
        String nombre = validarNombre(d.getNombreCategoria());
        categoriaRepository.findByNombreCategoriaIgnoreCase(nombre).ifPresent(otra -> {
            if (!otra.getIdCategoria().equals(id)) throw new RuntimeException("Ya existe una categoría con ese nombre.");
        });
        c.setNombreCategoria(nombre);
        if (d.getDescripcion() != null) c.setDescripcion(d.getDescripcion());
        return categoriaRepository.save(c);
    }

    @Transactional
    public void desactivar(Integer id) {
        Categoria c = obtenerPorId(id);
        long activos = productoRepository.countByCategoriaIdCategoriaAndActivoTrue(id);
        if (activos > 0) {
            throw new RuntimeException("No se puede eliminar: la categoría tiene " + activos + " producto(s) activo(s).");
        }
        c.setActivo(false);
        categoriaRepository.save(c);
    }

    private String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new RuntimeException("El nombre de la categoría es obligatorio.");
        return nombre.trim();
    }
}

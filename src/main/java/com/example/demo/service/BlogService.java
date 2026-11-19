package com.example.demo.service;

import com.example.demo.model.Blog;
import com.example.demo.model.BlogDetalle;
import com.example.demo.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogService {
    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public List<Blog> obtenerTodos() {
        return blogRepository.findByActivoTrue();
    }

    public Blog obtenerPorId(Integer id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog no encontrado"));
    }

    @Transactional
    public Blog guardar(Blog blog) {
        if (blog.getActivo() == null) blog.setActivo(true);
        if (blog.getDetalle() != null) blog.getDetalle().setBlog(blog);
        return blogRepository.save(blog);
    }

    // Actualiza un blog que ya existe: solo cambia los datos que llegaron
    @Transactional
    public Blog actualizar(Integer id, Blog datos) {
        // 1) Busca el blog guardado en la base de datos
        Blog blog = obtenerPorId(id);

        // 2) Cambia solo los campos que vienen con valor
        if (datos.getTitulo() != null && !datos.getTitulo().isBlank()) {
            blog.setTitulo(datos.getTitulo().trim());
        }
        if (datos.getDescripcionCorta() != null) {
            blog.setDescripcionCorta(datos.getDescripcionCorta());
        }
        // La imagen solo cambia si llega una nueva (si no, se conserva la actual)
        if (datos.getImagen() != null && !datos.getImagen().isBlank()) {
            blog.setImagen(datos.getImagen());
        }

        // 3) El texto largo vive en otra tabla (blog_detalle)
        String textoNuevo = datos.getDescripcionLarga();
        if (textoNuevo != null && !textoNuevo.isBlank()) {
            if (blog.getDetalle() != null) {
                // Ya tiene detalle: se modifica el mismo registro
                blog.getDetalle().setDescripcionLarga(textoNuevo);
            } else {
                // No tenía detalle: se crea y se enlaza con el blog
                BlogDetalle detalle = BlogDetalle.builder().descripcionLarga(textoNuevo).build();
                detalle.setBlog(blog);
                blog.setDetalle(detalle);
            }
        }

        return blogRepository.save(blog);
    }

    @Transactional
    public void desactivar(Integer id) {
        Blog b = obtenerPorId(id);
        b.setActivo(false);
        blogRepository.save(b);
    }
}
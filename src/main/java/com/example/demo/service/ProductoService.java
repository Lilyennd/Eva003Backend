package com.example.demo.service;

import com.example.demo.model.Categoria;
import com.example.demo.model.Producto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> obtenerTodos() { return productoRepository.findByActivoTrue(); }

    public List<Producto> obtenerEnOferta() { return productoRepository.findByActivoTrueAndEnOfertaTrue(); }

    public List<Producto> obtenerCriticos() { return productoRepository.findProductosCriticos(); }

    public Producto obtenerPorId(Integer id) {
        return productoRepository.findByIdProductoAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Transactional
    public Producto crear(Producto p) {
        String codigo = p.getCodigoProducto() == null ? "" : p.getCodigoProducto().trim();
        if (codigo.length() < 3) {
            throw new RuntimeException("El código es obligatorio y debe tener al menos 3 caracteres.");
        }
        if (productoRepository.existsByCodigoProducto(codigo)) {
            throw new RuntimeException("Ya existe un producto con el código " + codigo + ".");
        }
        validarCampos(p);
        p.setIdProducto(null);
        p.setCodigoProducto(codigo);
        p.setCategoria(resolverCategoria(p));
        p.setActivo(true);
        if (p.getEnOferta() == null) p.setEnOferta(false);
        if (!p.getEnOferta()) p.setPrecioOferta(null);
        if (p.getStockCritico() == null) p.setStockCritico(0);
        return productoRepository.save(p);
    }

    /** El código del producto no se modifica. */
    @Transactional
    public Producto actualizar(Integer id, Producto d) {
        Producto p = obtenerPorId(id);
        validarCampos(d);
        p.setNombre(d.getNombre());
        p.setDescripcion(d.getDescripcion());
        p.setPrecio(d.getPrecio());
        p.setEnOferta(Boolean.TRUE.equals(d.getEnOferta()));
        p.setPrecioOferta(p.getEnOferta() ? d.getPrecioOferta() : null);
        p.setStock(d.getStock());
        p.setStockCritico(d.getStockCritico() == null ? 0 : d.getStockCritico());
        p.setCategoria(resolverCategoria(d));
        p.setOrigen(d.getOrigen());
        p.setKilates(d.getKilates());
        p.setCorte(d.getCorte());
        p.setClaridad(d.getClaridad());
        p.setColor(d.getColor());
        p.setCertificado(d.getCertificado());
        p.setPesoGramos(d.getPesoGramos());
        p.setImagenPrincipal(d.getImagenPrincipal());
        return productoRepository.save(p);
    }

    @Transactional
    public Producto actualizarStock(Integer id, Integer nuevoStock) {
        if (nuevoStock == null || nuevoStock < 0) throw new RuntimeException("El stock no puede ser negativo.");
        Producto p = obtenerPorId(id);
        p.setStock(nuevoStock);
        return productoRepository.save(p);
    }

    @Transactional
    public void desactivar(Integer id) {
        Producto p = obtenerPorId(id);
        p.setActivo(false);
        productoRepository.save(p);
    }

    private void validarCampos(Producto p) {
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del producto es obligatorio.");
        }
        if (p.getPrecio() == null || p.getPrecio().signum() < 0) {
            throw new RuntimeException("El precio debe ser un número igual o mayor a cero.");
        }
        if (p.getStock() == null || p.getStock() < 0) {
            throw new RuntimeException("El stock debe ser un número entero y no puede ser negativo.");
        }
        if (Boolean.TRUE.equals(p.getEnOferta())
                && (p.getPrecioOferta() == null || p.getPrecioOferta().compareTo(p.getPrecio()) >= 0)) {
            throw new RuntimeException("El precio de oferta es obligatorio y debe ser menor al precio normal.");
        }
    }

    private Categoria resolverCategoria(Producto p) {
        if (p.getCategoria() == null || p.getCategoria().getIdCategoria() == null) {
            throw new RuntimeException("Debes seleccionar una categoría para el producto.");
        }
        return categoriaRepository.findById(p.getCategoria().getIdCategoria())
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .orElseThrow(() -> new RuntimeException("La categoría seleccionada no existe."));
    }
}

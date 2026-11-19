package com.example.demo.repository;

import com.example.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByActivoTrue();
    List<Producto> findByActivoTrueAndEnOfertaTrue();
    Optional<Producto> findByIdProductoAndActivoTrue(Integer id);
    boolean existsByCodigoProducto(String codigoProducto);
    long countByActivoTrue();
    long countByCategoriaIdCategoriaAndActivoTrue(Integer idCategoria);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= p.stockCritico")
    List<Producto> findProductosCriticos();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true AND p.stock <= p.stockCritico")
    long contarCriticos();

    @Query("SELECT SUM(p.stock) FROM Producto p WHERE p.activo = true")
    Long sumarStockActivo();
}

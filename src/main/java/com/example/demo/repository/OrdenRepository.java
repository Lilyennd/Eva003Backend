package com.example.demo.repository;

import com.example.demo.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Integer> {
    List<Orden> findByUsuarioIdUsuarioOrderByFechaOrdenDesc(Integer idUsuario);

    @Query("SELECT o FROM Orden o WHERE o.fechaOrden >= :desde AND o.fechaOrden < :hasta ORDER BY o.fechaOrden DESC")
    List<Orden> buscarPorRangoFechas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado = :estado")
    BigDecimal sumarTotalPorEstado(@Param("estado") String estado);
}

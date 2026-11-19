package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "codigo_producto", nullable = false, unique = true, length = 50)
    private String codigoProducto;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "precio_oferta", precision = 10, scale = 2)
    private BigDecimal precioOferta;

    @Column(name = "en_oferta", nullable = false)
    private Boolean enOferta;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "stock_critico")
    private Integer stockCritico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    private String origen;

    @Column(length = 20)
    private String kilates;

    private String corte;
    private String claridad;
    private String color;
    private String certificado;

    @Column(name = "peso_gramos", precision = 6, scale = 3)
    private BigDecimal pesoGramos;

    @Column(name = "imagen_principal")
    private String imagenPrincipal;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    // --- Campos planos para el React ---
    @JsonProperty("id_categoria")
    public Integer getIdCategoria() { return categoria != null ? categoria.getIdCategoria() : null; }

    @JsonProperty("id_categoria")
    public void setIdCategoria(Integer id) {
        if (id != null) this.categoria = Categoria.builder().idCategoria(id).build();
    }

    @JsonProperty(value = "nombre_categoria", access = JsonProperty.Access.READ_ONLY)
    public String getNombreCategoria() { return categoria != null ? categoria.getNombreCategoria() : null; }
}

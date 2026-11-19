package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Integer idOrden;

    @Column(name = "numero_orden", nullable = false, unique = true, length = 30)
    private String numeroOrden;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "nombre_cliente", nullable = false, length = 50)
    private String nombreCliente;

    @Column(name = "apellidos_cliente", nullable = false, length = 100)
    private String apellidosCliente;

    @Column(name = "correo_cliente", nullable = false, length = 100)
    private String correoCliente;

    @Column(nullable = false, length = 150)
    private String calle;

    @Column(length = 30)
    private String departamento;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region", nullable = false)
    private Region region;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comuna", nullable = false)
    private Comuna comuna;

    @Column(length = 255)
    private String indicaciones;

    @Column(name = "opcion_entrega", nullable = false, length = 30)
    private String opcionEntrega;

    @Column(nullable = false, length = 20)
    private String estado;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "costo_envio", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_orden", insertable = false, updatable = false)
    private LocalDateTime fechaOrden;

    // El front lee "detalle"
    @JsonProperty("detalle")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenDetalle> detalles = new ArrayList<>();

    @JsonProperty(value = "id_usuario", access = JsonProperty.Access.READ_ONLY)
    public Integer getIdUsuario() { return usuario != null ? usuario.getIdUsuario() : null; }

    @JsonProperty(value = "nombre_region", access = JsonProperty.Access.READ_ONLY)
    public String getNombreRegion() { return region != null ? region.getNombreRegion() : null; }

    @JsonProperty(value = "nombre_comuna", access = JsonProperty.Access.READ_ONLY)
    public String getNombreComuna() { return comuna != null ? comuna.getNombreComuna() : null; }
}

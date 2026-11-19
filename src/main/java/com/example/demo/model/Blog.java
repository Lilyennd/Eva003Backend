package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_blog")
    private Integer idBlog;

    @Column(nullable = false, unique = true, length = 200)
    private String titulo;

    @Column(name = "descripcion_corta", length = 500)
    private String descripcionCorta;

    private String imagen;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_publicacion", insertable = false, updatable = false)
    private LocalDateTime fechaPublicacion;

    // El objeto detalle no se muestra en el JSON: se expone como campo plano (abajo)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private BlogDetalle detalle;

    // --- Campo plano para el React: descripcion_larga ---
    @JsonProperty("descripcion_larga")
    public String getDescripcionLarga() {
        return detalle != null ? detalle.getDescripcionLarga() : null;
    }

    @JsonProperty("descripcion_larga")
    public void setDescripcionLarga(String texto) {
        if (texto == null) return;
        if (detalle == null) {
            // Crea el detalle la primera vez; el service lo enlaza con el blog
            detalle = BlogDetalle.builder().descripcionLarga(texto).build();
        } else {
            detalle.setDescripcionLarga(texto);
        }
    }
}
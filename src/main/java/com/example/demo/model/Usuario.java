package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(nullable = false, unique = true, length = 9)
    private String run;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    // Nunca sale en el JSON de respuesta
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region")
    private Region region;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comuna")
    private Comuna comuna;

    @Column(length = 300)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    // --- Ids planos para el React (id_rol, id_region, id_comuna) ---
    @JsonProperty("id_rol")
    public Integer getIdRol() { return rol != null ? rol.getIdRol() : null; }

    @JsonProperty("id_rol")
    public void setIdRol(Integer id) { if (id != null) this.rol = Rol.builder().idRol(id).build(); }

    @JsonProperty("id_region")
    public Integer getIdRegion() { return region != null ? region.getIdRegion() : null; }

    @JsonProperty("id_region")
    public void setIdRegion(Integer id) { if (id != null) this.region = Region.builder().idRegion(id).build(); }

    @JsonProperty("id_comuna")
    public Integer getIdComuna() { return comuna != null ? comuna.getIdComuna() : null; }

    @JsonProperty("id_comuna")
    public void setIdComuna(Integer id) { if (id != null) this.comuna = Comuna.builder().idComuna(id).build(); }
}

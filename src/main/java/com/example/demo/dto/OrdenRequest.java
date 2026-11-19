package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrdenRequest {
    private String nombre;
    private String apellidos;
    private String correo;
    private String calle;
    private String departamento;
    private String indicaciones;
    private Integer idRegion;       // JSON: id_region
    private Integer idComuna;       // JSON: id_comuna
    private String opcionEntrega;   // JSON: opcion_entrega
    private List<Item> items;

    @Data
    public static class Item {
        private Integer idProducto; // JSON: id_producto
        private Integer cantidad;
    }
}

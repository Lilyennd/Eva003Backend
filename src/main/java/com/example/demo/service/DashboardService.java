package com.example.demo.service;

import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContactoRepository contactoRepository;

    public DashboardService(OrdenRepository ordenRepository, ProductoRepository productoRepository,
                            UsuarioRepository usuarioRepository, ContactoRepository contactoRepository) {
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.contactoRepository = contactoRepository;
    }

    public Map<String, Object> obtenerMetricas() {
        BigDecimal ingresos = ordenRepository.sumarTotalPorEstado("Pagada");
        Long unidades = productoRepository.sumarStockActivo();

        // Las claves son las mismas que lee el front-end en AdminDashboard.jsx
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("compras", ordenRepository.count());
        m.put("ingresos", ingresos != null ? ingresos : BigDecimal.ZERO);
        m.put("productos", productoRepository.countByActivoTrue());
        m.put("inventario_unidades", unidades != null ? unidades : 0L);
        m.put("usuarios", usuarioRepository.countByActivoTrue());
        m.put("usuarios_nuevos_30d",
                usuarioRepository.countByActivoTrueAndFechaRegistroAfter(LocalDateTime.now().minusDays(30)));
        m.put("productos_criticos", productoRepository.contarCriticos());
        m.put("mensajes_sin_leer", contactoRepository.countByLeidoFalse());
        return m;
    }
}

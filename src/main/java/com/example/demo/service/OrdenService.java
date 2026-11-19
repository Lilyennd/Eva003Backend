package com.example.demo.service;

import com.example.demo.dto.OrdenRequest;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrdenService {
    static final List<String> ESTADOS = List.of(
            "Pendiente", "Procesando", "Pagada", "Pago fallido", "Enviado", "Entregado", "Cancelado");

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;

    public OrdenService(OrdenRepository ordenRepository, UsuarioRepository usuarioRepository,
                        ProductoRepository productoRepository, RegionRepository regionRepository,
                        ComunaRepository comunaRepository) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
    }

    public List<Orden> obtenerTodas() {
        return ordenRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaOrden"));
    }

    public List<Orden> obtenerPorUsuario(Integer idUsuario) {
        return ordenRepository.findByUsuarioIdUsuarioOrderByFechaOrdenDesc(idUsuario);
    }

    public Orden obtenerPorId(Integer id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    /** Órdenes dentro de un rango de fechas (inclusive). Si falta una, se usa un valor abierto. */
    public List<Orden> obtenerPorFechas(LocalDate desde, LocalDate hasta) {
        LocalDate d = desde != null ? desde : LocalDate.of(2000, 1, 1);
        LocalDate h = hasta != null ? hasta : LocalDate.now();
        if (d.isAfter(h)) throw new RuntimeException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        LocalDateTime inicio = d.atStartOfDay();
        LocalDateTime fin = h.plusDays(1).atStartOfDay();
        return ordenRepository.buscarPorRangoFechas(inicio, fin);
    }

    /** Crea la orden calculando todo desde la BD: el total que calcule el front no se usa. */
    @Transactional
    public Orden crear(OrdenRequest req, Integer idUsuario) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }
        if (req.getIdRegion() == null || req.getIdComuna() == null) {
            throw new RuntimeException("Debes indicar región y comuna.");
        }
        Usuario usuario = idUsuario == null ? null : usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Region region = regionRepository.findById(req.getIdRegion())
                .orElseThrow(() -> new RuntimeException("Región inválida"));
        Comuna comuna = comunaRepository.findById(req.getIdComuna())
                .orElseThrow(() -> new RuntimeException("Comuna inválida"));
        if (!comuna.getRegion().getIdRegion().equals(region.getIdRegion())) {
            throw new RuntimeException("La comuna no pertenece a la región seleccionada.");
        }

        Orden orden = Orden.builder()
                .numeroOrden(generarNumero())
                .usuario(usuario)
                .nombreCliente(req.getNombre())
                .apellidosCliente(req.getApellidos())
                .correoCliente(req.getCorreo())
                .calle(req.getCalle())
                .departamento(req.getDepartamento())
                .region(region)
                .comuna(comuna)
                .indicaciones(req.getIndicaciones())
                .opcionEntrega(req.getOpcionEntrega() == null ? "Despacho" : req.getOpcionEntrega())
                .estado("Pendiente")
                .build();

        List<OrdenDetalle> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (OrdenRequest.Item item : req.getItems()) {
            if (item.getCantidad() == null || item.getCantidad() < 1) {
                throw new RuntimeException("Cantidad inválida.");
            }
            Producto p = productoRepository.findByIdProductoAndActivoTrue(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            if (p.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente de " + p.getNombre());
            }
            BigDecimal precio = Boolean.TRUE.equals(p.getEnOferta()) && p.getPrecioOferta() != null
                    ? p.getPrecioOferta() : p.getPrecio();
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
            detalles.add(OrdenDetalle.builder()
                    .orden(orden)
                    .producto(p)
                    .nombreProducto(p.getNombre())
                    .imagenProducto(p.getImagenPrincipal())
                    .cantidad(item.getCantidad())
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build());
        }
        orden.setDetalles(detalles);
        orden.setTotal(total);
        return ordenRepository.save(orden);
    }

    /**
     * Cliente: solo su orden, solo Pendiente -> Pagada / Pago fallido.
     * Admin: cualquier estado. El stock baja al pagar y se repone si una orden pagada se cancela.
     */
    @Transactional
    public Orden cambiarEstado(Integer id, String estado, Integer idActual, boolean esAdmin) {
        if (estado == null || !ESTADOS.contains(estado)) {
            throw new RuntimeException("Estado inválido.");
        }
        Orden orden = obtenerPorId(id);
        if (!esAdmin) {
            boolean suya = orden.getUsuario() != null && orden.getUsuario().getIdUsuario().equals(idActual);
            if (!suya) throw new AccessDeniedException("No tienes acceso a esta orden.");
            if (!estado.equals("Pagada") && !estado.equals("Pago fallido")) {
                throw new AccessDeniedException("Estado no permitido.");
            }
            if (!"Pendiente".equals(orden.getEstado())) {
                throw new RuntimeException("La orden ya fue procesada.");
            }
        }
        String anterior = orden.getEstado();
        if (estado.equals("Pagada") && !"Pagada".equals(anterior)) {
            for (OrdenDetalle d : orden.getDetalles()) {
                Producto p = d.getProducto();
                if (p.getStock() < d.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente de " + p.getNombre());
                }
                p.setStock(p.getStock() - d.getCantidad());
            }
        } else if (estado.equals("Cancelado") && "Pagada".equals(anterior)) {
            for (OrdenDetalle d : orden.getDetalles()) {
                Producto p = d.getProducto();
                p.setStock(p.getStock() + d.getCantidad());
            }
        }
        orden.setEstado(estado);
        return ordenRepository.save(orden);
    }

    private String generarNumero() {
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
}

package com.example.demo.service;

import com.example.demo.dto.OrdenRequest;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {
    @Mock OrdenRepository ordenRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock ProductoRepository productoRepository;
    @Mock RegionRepository regionRepository;
    @Mock ComunaRepository comunaRepository;
    @InjectMocks OrdenService service;

    private final Region region = Region.builder().idRegion(7).nombreRegion("RM").build();
    private final Comuna comuna = Comuna.builder().idComuna(3).nombreComuna("Santiago").region(region).build();

    private Producto producto(int stock, boolean oferta) {
        return Producto.builder().idProducto(1).nombre("Diamante").precio(new BigDecimal("1000"))
                .precioOferta(new BigDecimal("800")).enOferta(oferta).stock(stock).activo(true).build();
    }

    private OrdenRequest request(int cantidad) {
        OrdenRequest.Item item = new OrdenRequest.Item();
        item.setIdProducto(1);
        item.setCantidad(cantidad);
        OrdenRequest r = new OrdenRequest();
        r.setNombre("Ana"); r.setApellidos("Pérez"); r.setCorreo("ana@gmail.com"); r.setCalle("Calle 1");
        r.setIdRegion(7); r.setIdComuna(3); r.setItems(List.of(item));
        return r;
    }

    private void stubUbicacion() {
        when(regionRepository.findById(7)).thenReturn(Optional.of(region));
        when(comunaRepository.findById(3)).thenReturn(Optional.of(comuna));
    }

    @Test
    void crear_usaPrecioDeOfertaYCalculaTotal() {
        stubUbicacion();
        when(productoRepository.findByIdProductoAndActivoTrue(1)).thenReturn(Optional.of(producto(10, true)));
        when(ordenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Orden o = service.crear(request(2), null);

        assertEquals(new BigDecimal("1600"), o.getTotal());
        assertEquals("Pendiente", o.getEstado());
        assertEquals(1, o.getDetalles().size());
    }

    @Test
    void crear_rechazaStockInsuficiente() {
        stubUbicacion();
        when(productoRepository.findByIdProductoAndActivoTrue(1)).thenReturn(Optional.of(producto(1, false)));

        RuntimeException e = assertThrows(RuntimeException.class, () -> service.crear(request(5), null));
        assertTrue(e.getMessage().contains("Stock insuficiente"));
    }

    @Test
    void pagar_descuentaStock() {
        Producto p = producto(10, false);
        Orden orden = Orden.builder().idOrden(1).estado("Pendiente").build();
        orden.getDetalles().add(OrdenDetalle.builder().orden(orden).producto(p).cantidad(3).build());
        when(ordenRepository.findById(1)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.cambiarEstado(1, "Pagada", 99, true);

        assertEquals(7, p.getStock());
        assertEquals("Pagada", orden.getEstado());
    }
}

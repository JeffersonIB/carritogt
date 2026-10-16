package com.carritogt.service;

import com.carritogt.dto.CarritoResponse;
import com.carritogt.entity.CodigoUsuario;
import com.carritogt.entity.ItemCarrito;
import com.carritogt.entity.Producto;
import com.carritogt.exception.RecursoNoEncontradoException;
import com.carritogt.exception.ReglaNegocioException;
import com.carritogt.mapper.CarritoMapper;
import com.carritogt.repository.ItemCarritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de CarritoService.
 * Los repositorios se reemplazan por dobles de prueba (Mockito): no se usa base de datos.
 */
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    private static final CodigoUsuario USUARIO = CodigoUsuario.de("3f2b8c1e-9d4a-4c7b-8e2f-1a2b3c4d5e6f");

    @Mock
    private ItemCarritoRepository itemRepository;

    @Mock
    private ProductoService productoService;

    private CarritoService carritoService;

    private final Producto camara = new Producto("FinePix Pro2 3D Camera", "3DcAM01",
            "images/product/camera.jpg", new BigDecimal("671.21"));
    private final Producto disco = new Producto("EXP Portable Hard Drive", "USB02",
            "images/product/external-hard-drive.jpg", new BigDecimal("215.10"));

    @BeforeEach
    void setUp() {
        carritoService = new CarritoService(itemRepository, productoService, new CarritoMapper());
    }

    @Test
    void agregarProductoNuevoGuardaUnArticulo() {
        when(productoService.buscarEntidad(1)).thenReturn(camara);
        when(itemRepository.findFirstByCodUsuarioAndProducto_IdProducto(any(), any())).thenReturn(Optional.empty());
        when(itemRepository.buscarPorUsuario(USUARIO.valor()))
                .thenReturn(List.of(new ItemCarrito(USUARIO.valor(), camara, 2)));

        CarritoResponse carrito = carritoService.agregarProducto(USUARIO, 1, 2);

        ArgumentCaptor<ItemCarrito> guardado = ArgumentCaptor.forClass(ItemCarrito.class);
        verify(itemRepository).save(guardado.capture());
        assertThat(guardado.getValue().getCantidad()).isEqualTo(2);
        assertThat(guardado.getValue().getCodUsuario()).isEqualTo(USUARIO.valor());
        assertThat(carrito.total()).isEqualByComparingTo("1342.42");
    }

    @Test
    void agregarProductoExistenteAcumulaLaCantidad() {
        ItemCarrito existente = new ItemCarrito(USUARIO.valor(), camara, 1);
        when(productoService.buscarEntidad(1)).thenReturn(camara);
        when(itemRepository.findFirstByCodUsuarioAndProducto_IdProducto(any(), any()))
                .thenReturn(Optional.of(existente));
        when(itemRepository.buscarPorUsuario(USUARIO.valor())).thenReturn(List.of(existente));

        CarritoResponse carrito = carritoService.agregarProducto(USUARIO, 1, 2);

        assertThat(existente.getCantidad()).isEqualTo(3);
        assertThat(carrito.totalArticulos()).isEqualTo(3);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void agregarProductoInexistenteLanzaExcepcionYNoGuarda() {
        when(productoService.buscarEntidad(999))
                .thenThrow(new RecursoNoEncontradoException("No existe el producto con id 999"));

        assertThatThrownBy(() -> carritoService.agregarProducto(USUARIO, 999, 1))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void agregarQueSuperaElMaximoLanzaReglaDeNegocio() {
        ItemCarrito existente = new ItemCarrito(USUARIO.valor(), camara, 98);
        when(productoService.buscarEntidad(1)).thenReturn(camara);
        when(itemRepository.findFirstByCodUsuarioAndProducto_IdProducto(any(), any()))
                .thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> carritoService.agregarProducto(USUARIO, 1, 5))
                .isInstanceOf(ReglaNegocioException.class);
        assertThat(existente.getCantidad()).isEqualTo(98);
    }

    @Test
    void obtenerCarritoCalculaTotalYArticulos() {
        when(itemRepository.buscarPorUsuario(USUARIO.valor())).thenReturn(List.of(
                new ItemCarrito(USUARIO.valor(), camara, 3),
                new ItemCarrito(USUARIO.valor(), disco, 1)));

        CarritoResponse carrito = carritoService.obtenerCarrito(USUARIO);

        assertThat(carrito.items()).hasSize(2);
        assertThat(carrito.totalArticulos()).isEqualTo(4);
        assertThat(carrito.total()).isEqualByComparingTo("2228.73");
    }

    @Test
    void eliminarArticuloDeOtroUsuarioLanzaNoEncontrado() {
        when(itemRepository.findByIdItemAndCodUsuario(10, USUARIO.valor())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carritoService.eliminarItem(USUARIO, 10))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("no existe en su carrito");
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void actualizarCantidadCambiaElArticulo() {
        ItemCarrito existente = new ItemCarrito(USUARIO.valor(), disco, 4);
        when(itemRepository.findByIdItemAndCodUsuario(7, USUARIO.valor())).thenReturn(Optional.of(existente));
        when(itemRepository.buscarPorUsuario(USUARIO.valor())).thenReturn(List.of(existente));

        CarritoResponse carrito = carritoService.actualizarCantidad(USUARIO, 7, 2);

        assertThat(existente.getCantidad()).isEqualTo(2);
        assertThat(carrito.total()).isEqualByComparingTo("430.20");
    }

    @Test
    void vaciarEliminaLosArticulosDelUsuario() {
        carritoService.vaciar(USUARIO);

        verify(itemRepository).eliminarPorUsuario(USUARIO.valor());
    }
}

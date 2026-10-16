package com.carritogt.entity;

import com.carritogt.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Pruebas unitarias de las reglas de negocio que viven en la entidad ItemCarrito. */
class ItemCarritoTest {

    private final Producto camara = new Producto("FinePix Pro2 3D Camera", "3DcAM01",
            "images/product/camera.jpg", new BigDecimal("671.21"));

    @Test
    void subtotalEsPrecioPorCantidad() {
        ItemCarrito item = new ItemCarrito("usuario-prueba-1", camara, 3);

        assertThat(item.getSubtotal()).isEqualByComparingTo("2013.63");
    }

    @Test
    void aumentarCantidadAcumulaUnidades() {
        ItemCarrito item = new ItemCarrito("usuario-prueba-1", camara, 2);

        item.aumentarCantidad(5);

        assertThat(item.getCantidad()).isEqualTo(7);
    }

    @Test
    void cambiarCantidadReemplazaElValor() {
        ItemCarrito item = new ItemCarrito("usuario-prueba-1", camara, 10);

        item.cambiarCantidad(1);

        assertThat(item.getCantidad()).isEqualTo(1);
        assertThat(item.getSubtotal()).isEqualByComparingTo("671.21");
    }

    @Test
    void noPermiteCrearConCantidadCero() {
        assertThatThrownBy(() -> new ItemCarrito("usuario-prueba-1", camara, 0))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void noPermiteSuperarLaCantidadMaxima() {
        ItemCarrito item = new ItemCarrito("usuario-prueba-1", camara, ItemCarrito.CANTIDAD_MAXIMA);

        assertThatThrownBy(() -> item.aumentarCantidad(1))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("entre 1 y 99");
        assertThat(item.getCantidad()).isEqualTo(ItemCarrito.CANTIDAD_MAXIMA);
    }

    @Test
    void noPermiteAumentarConUnidadesNegativas() {
        ItemCarrito item = new ItemCarrito("usuario-prueba-1", camara, 5);

        assertThatThrownBy(() -> item.aumentarCantidad(-2))
                .isInstanceOf(ReglaNegocioException.class);
        assertThat(item.getCantidad()).isEqualTo(5);
    }
}

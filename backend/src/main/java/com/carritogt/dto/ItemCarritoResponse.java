package com.carritogt.dto;

import java.math.BigDecimal;

/** Un articulo del carrito con su subtotal calculado en el servidor. */
public record ItemCarritoResponse(
        Integer idItem,
        Integer idProducto,
        String nombre,
        String codigo,
        String imagen,
        BigDecimal precio,
        Integer cantidad,
        BigDecimal subtotal) {
}

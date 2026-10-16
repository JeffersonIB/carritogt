package com.carritogt.dto;

import java.math.BigDecimal;
import java.util.List;

/** Contenido completo del carrito (RF-04): articulos, total de unidades y total a pagar. */
public record CarritoResponse(
        List<ItemCarritoResponse> items,
        int totalArticulos,
        BigDecimal total) {
}

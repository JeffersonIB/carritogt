package com.carritogt.dto;

import java.math.BigDecimal;

/** Producto del catalogo tal como lo recibe el frontend (RF-01, RF-02, RF-07). */
public record ProductoResponse(
        Integer id,
        String nombre,
        String codigo,
        String imagen,
        BigDecimal precio) {
}

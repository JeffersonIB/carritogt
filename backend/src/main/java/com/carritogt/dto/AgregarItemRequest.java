package com.carritogt.dto;

import com.carritogt.entity.ItemCarrito;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Cuerpo de POST /api/carrito/items (RF-03).
 * El precio NO se recibe del navegador: siempre se toma de la base de datos.
 */
public record AgregarItemRequest(
        @NotNull(message = "es obligatorio")
        @Positive(message = "debe ser un numero positivo")
        Integer idProducto,

        @NotNull(message = "es obligatoria")
        @Min(value = 1, message = "debe ser al menos 1")
        @Max(value = ItemCarrito.CANTIDAD_MAXIMA, message = "no puede ser mayor que 99")
        Integer cantidad) {
}

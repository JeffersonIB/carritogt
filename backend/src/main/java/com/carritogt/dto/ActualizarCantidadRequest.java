package com.carritogt.dto;

import com.carritogt.entity.ItemCarrito;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Cuerpo de PUT /api/carrito/items/{idItem} (mejora opcional: modificar cantidades). */
public record ActualizarCantidadRequest(
        @NotNull(message = "es obligatoria")
        @Min(value = 1, message = "debe ser al menos 1")
        @Max(value = ItemCarrito.CANTIDAD_MAXIMA, message = "no puede ser mayor que 99")
        Integer cantidad) {
}

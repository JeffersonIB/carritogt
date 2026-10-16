package com.carritogt.dto;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;

/** Formato JSON unico para todas las respuestas de error de la API. */
public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        List<String> detalles) {

    public static ErrorResponse de(HttpStatus estado, String mensaje, String ruta, List<String> detalles) {
        return new ErrorResponse(OffsetDateTime.now(), estado.value(), estado.getReasonPhrase(),
                mensaje, ruta, detalles);
    }
}

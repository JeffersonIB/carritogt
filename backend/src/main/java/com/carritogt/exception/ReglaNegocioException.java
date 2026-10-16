package com.carritogt.exception;

import org.springframework.http.HttpStatus;

/**
 * Los datos tienen formato correcto, pero violan una regla del carrito
 * (por ejemplo, superar 99 unidades de un producto). HTTP 422.
 */
public class ReglaNegocioException extends CarritoGtException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

    @Override
    public HttpStatus getEstado() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}

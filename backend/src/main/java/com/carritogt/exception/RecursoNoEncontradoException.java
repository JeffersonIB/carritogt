package com.carritogt.exception;

import org.springframework.http.HttpStatus;

/** El producto o el articulo del carrito no existe (HTTP 404). */
public class RecursoNoEncontradoException extends CarritoGtException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    @Override
    public HttpStatus getEstado() {
        return HttpStatus.NOT_FOUND;
    }
}

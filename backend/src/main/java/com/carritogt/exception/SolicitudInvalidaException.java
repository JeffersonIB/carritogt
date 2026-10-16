package com.carritogt.exception;

import org.springframework.http.HttpStatus;

/** La solicitud tiene datos con formato incorrecto (HTTP 400). */
public class SolicitudInvalidaException extends CarritoGtException {

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }

    @Override
    public HttpStatus getEstado() {
        return HttpStatus.BAD_REQUEST;
    }
}

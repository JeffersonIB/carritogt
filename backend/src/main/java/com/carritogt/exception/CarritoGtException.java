package com.carritogt.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepcion base (abstracta) del sistema.
 *
 * Polimorfismo: cada subclase decide su propio codigo HTTP en getEstado().
 * El manejador global atiende a todas con un solo metodo, sin preguntar el tipo concreto.
 */
public abstract class CarritoGtException extends RuntimeException {

    protected CarritoGtException(String mensaje) {
        super(mensaje);
    }

    /** Codigo HTTP con el que la API debe responder. */
    public abstract HttpStatus getEstado();
}

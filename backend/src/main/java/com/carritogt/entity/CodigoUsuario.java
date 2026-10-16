package com.carritogt.entity;

import com.carritogt.exception.SolicitudInvalidaException;

import java.util.regex.Pattern;

/**
 * Objeto de valor del dominio: identificador anonimo del comprador.
 *
 * En PHP se generaba un GUID y se guardaba en la sesion ($_SESSION['codUsr']).
 * En la API REST (sin estado) el navegador genera el GUID, lo guarda en localStorage
 * y lo envia en el encabezado HTTP "X-Usuario". Esta clase valida su formato una sola vez,
 * de modo que el resto del sistema siempre recibe un codigo correcto.
 */
public record CodigoUsuario(String valor) {

    private static final Pattern FORMATO = Pattern.compile("^[A-Za-z0-9-]{8,128}$");

    public CodigoUsuario {
        if (valor == null || !FORMATO.matcher(valor).matches()) {
            throw new SolicitudInvalidaException(
                    "El encabezado X-Usuario debe tener entre 8 y 128 caracteres (letras, numeros o guiones)");
        }
    }

    public static CodigoUsuario de(String valor) {
        return new CodigoUsuario(valor);
    }
}

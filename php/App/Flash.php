<?php

namespace App;

/**
 * Mensajes de notificación: mensajes para mostrar una sola vez usando las variables 
 * de sesión entre solicitudes.
 *
 * PHP version 7.0
 */
class Flash {
    /**
     * Tipo de mensaje: Exito
     * @var string
     */
    const EXITO = 'exito';

    /**
     * Tipo de mensaje: Información
     * @var string
     */
    const INFO = 'info';

    /**
     * Tipo de mensaje: Aviso
     * @var string
     */
    const AVISO = 'aviso';

    /**
     * Tipo de mensaje: Error
     * @var string
     */
    const ERROR = 'error';

    /**
     * Agregar un mensaje
     *
     * @param string $mensaje  El contenido del mensaje
     * @param string $tipo El tipo de mensaje, opcional. Por defecto EXITO
     *
     * @return void
     */
    public static function agregarMensaje( $mensaje, $tipo = 'exito', $alt = '' ) {
        // Crear un arreglo en la sesión si aún no esta creado
        if (! isset($_SESSION['flash_notificaciones'])) {
            $_SESSION[ 'flash_notificaciones' ] = [];
        }

        // Agregar el mensaje al arreglo
        $_SESSION[ 'flash_notificaciones' ][] = [
            'cuerpo'    => $mensaje,
            'tipo'      => $tipo,
            'alt'       => $alt
        ];
    }

    /**
     * Obtener todos los mensajes
     *
     * @return mixed  Un arreglo con todos los mensaje, o, null si esta vacío
     */
    public static function getMensajes() {
        if (isset( $_SESSION[ 'flash_notificaciones' ] ) ) {
            $messages = $_SESSION[ 'flash_notificaciones' ];
            unset($_SESSION[ 'flash_notificaciones' ]);

            return $messages;
        }
    }
}
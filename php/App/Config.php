<?php

namespace App;

/**
 * Configuración de la aplicación
 *
 * PHP version 7.0
 */

//define( 'HTTP_HOST', $_SERVER[ 'HTTP_HOST' ] );

class Config {

    /**
     * Base de datos, host
     * @var string
     */
    const DB_HOST = 'localhost';

    /**
     * Base de datos, nombre de base de datos
     * @var string
     */
    const DB_NAME = 'cc';

    /**
     * Base de datos, usuario
     * @var string
     */
    const DB_USER = 'root';

    /**
     * Base de datos, contraseña
     * @var string
     */
    const DB_PASSWORD = 'CAMBIAR_EN_LOCAL';

    /**
     * Muestra u oculta los mensajes de error en pantalla
     * @var boolean
     */
    const SHOW_ERRORS = true;

    /**
     * Nombre del Host de la maquina
     * @var string
     */
    public const HOST = 'carrito-compra.localhost'; //HTTP_HOST;
}

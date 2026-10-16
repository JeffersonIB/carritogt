<?php

/**
 * Front controller
 *
 * PHP version 7.0
 */

/**
 * Composer
 */
require dirname(__DIR__) . '/vendor/autoload.php';


/**
 * Error and Exception handling
 */
error_reporting(E_ALL);
set_error_handler('Core\Error::errorHandler');
set_exception_handler('Core\Error::exceptionHandler');

/**
 * Sesiones
 */
//ini_set( 'session.cookie_lifetime', 0 );
//ini_set( 'session.use_only_cookies', 0 );

session_start();

/**
 * Routing
 */
$router = new Core\Router();

// Add the routes
$router->add( '', ['controller' => 'Home', 'action' => 'index'] );
$router->add( '{controller}', [ 'action' => 'index' ]);
$router->add( '{controller}/{action}' );
$router->add( '{controller}/{id:\d+}/{action}' );
$router->add( '{controller}/{token:[\da-f\-]+}/{action}' );
    
$router->dispatch( $_SERVER['QUERY_STRING'] );
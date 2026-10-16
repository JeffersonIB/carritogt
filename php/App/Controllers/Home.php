<?php

namespace App\Controllers;

use \Core\View;
use \App\Models\ProductoModel;

/**
 * Home controller
 *
 * PHP version 7.0
 */
class Home extends \Core\Controller {

    /**
     * Muestra la página principal la tienda en línea
     *
     * @return void
     */
    public function indexAction() {
        $productos = [];
        $productos = ProductoModel::getAll();
        View::renderTemplate('Home/index.html'
            , [
                'productos' => $productos
            ]
        );
    }
}
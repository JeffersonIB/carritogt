<?php

namespace App\Controllers;

use \Core\View;
use \App\Auth;
use \App\Flash;
use \App\Models\ProductoModel;
use \App\Models\ShoppingCartModel;

/**
 * Controlador de ShoppingCart
 *
 * PHP version 7.0
 */
class ShoppingCart extends \Core\Controller {
    /**
     * Muestra la página principal de carrito de compra
     *
     * @return void
     */
    public function indexAction()
    {
        View::renderTemplate('ShoppingCart/index.html'
            , [
                'productos'		=> ShoppingCartModel::getAll( Auth::getUsuario() )
            ]
        );
    }

    /**
     * Agrega un elemento al carrito de compra
     *
     * @return void
     */
    public function agregarAction() {
        if(empty($_POST))

            View::renderTemplate('Home/index.html', [
			    'productos'		=> ProductoModel::getAll()
		    ]);

        $cod_usr = $this->route_params[ 'token' ];
        $item = new ProductoModel($_POST);
    
        ShoppingCartModel::agregar( $cod_usr, $item );

        Flash::agregarMensaje( 'Producto agregado', FLASH::EXITO, '2' );
		$this->redirect( '/' );

        //View::renderTemplate('ShoppingCart/index.html', [
		//	'productos'		=> ShoppingCartModel::getAll(Auth::getUsuario())
		//]);
    }

    /**
     * Eliminar un elemento del carrito de compra
     *
     * @return void
     */
    public function eliminarAction() {
        $id = $this->route_params[ 'id' ];
    
        ShoppingCartModel::eliminar( $id );

        View::renderTemplate('ShoppingCart/index.html', [
			'productos'		=> ShoppingCartModel::getAll(Auth::getUsuario())
		]);
    }

    /**
     * Vacía completamente el carrito de compra
     *
     * @return void
     */
    public function vaciarAction() {
        $id = $this->route_params[ 'token' ];
    
        ShoppingCartModel::eliminarTodos( $id );

        View::renderTemplate('ShoppingCart/index.html', [
			'productos'		=> ShoppingCartModel::getAll(Auth::getUsuario())
		]);
    }
}

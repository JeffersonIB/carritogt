<?php

namespace App\Models;

use PDO;

/**
 * Modelo Producto
 *
 * PHP version 7.0
 */
class ShoppingCartModel extends \Core\Model
{

    /**
     * Obtiene todos los productos como un arreglo
     * @return array
     */

    public static function getAll( $cod_usr )
    {
        $db = static::getDB();
        $stmt = $db->query("SELECT sc.idShoppingCart as idsc, p.*, sc.cantidad, p.precio*sc.cantidad AS sub_total FROM shopping_cart sc INNER JOIN producto p ON sc.idProducto = p.idProducto WHERE codUsuario like '$cod_usr'");
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public static function agregar( $cod_usr, $item ) {

        $res = static::gld( "shopping_cart"
            , [
            "idProducto"	=> $item->id
            , "codUsuario"  => $cod_usr
            ]
            , [
                "cantidad"	=> "cantidad + " . $item->cantidad
            ]
            , [
                "idProducto"	=> $item->id
                , "codUsuario"	=> $cod_usr
                , "cantidad"	=> $item->cantidad
            ]);
        
        return $res;
    }

    public static function eliminar( $idShoppingCart ) {

        $res = new Resultado( 1, Resultado::NO, "No es posible eliminar item del carrito de compra" );
    
        $sql = "DELETE FROM `shopping_cart` WHERE idShoppingCart = $idShoppingCart";
    
        $_db = static::getDB();
        $stm = $_db->prepare( $sql );

        $stm->execute();
    
        $res->__set( "errno", 0 )->__set( "resultado", Resultado::OK );
        $res->__set( "mensaje", "Item eliminado" );
    
        return $res;
    }

    public static function eliminarTodos( $cod_usr ) {

        $res = new Resultado( 1, Resultado::NO, "No es posible vacíar el carrito de compra" );
    
        $sql = "DELETE FROM `shopping_cart` WHERE codUsuario LIKE :cod";
        $_db = static::getDB();
        $stm = $_db->prepare( $sql );
        $stm->bindParam( ':cod', $cod_usr, PDO::PARAM_STR );

        $stm->execute();
    
        $res->__set( "errno", 0 )->__set( "resultado", Resultado::OK );
        $res->__set( "mensaje", "Carrito de compra vacíado" );
    
        return $res;
    }
}

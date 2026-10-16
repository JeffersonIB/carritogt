<?php

namespace App\Models;

use PDO;

/**
 * Modelo Producto
 *
 * PHP version 7.0
 */
class ProductoModel extends \Core\Model {

    public function __construct( $data = [] ) {
        foreach( $data as $key => $value ) {
          $this -> $key = $value;
        };
    }

    /**
     * Obtiene todos los productos como un arreglo
     * @return array
     */

    public static function getAll() {
        $db = static::getDB();
        $stmt = $db->query( "SELECT idProducto, nombre, codigo, imagen, precio FROM producto" );
        return $stmt->fetchAll( PDO::FETCH_ASSOC );
    }
}

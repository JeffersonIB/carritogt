<?php

namespace Core;

use PDO;
use App\Config;
use App\Models\Resultado;

/**
 * Base model
 *
 * PHP version 7.0
 */
abstract class Model {
    /**
     * Get the PDO database connection
     *
     * @return mixed
     */
    protected static function getDB() {
        static $db = null;

        if ($db === null) {
            $dsn = 'mysql:host=' . Config::DB_HOST . ';dbname=' . Config::DB_NAME . ';charset=utf8';
            $db = new PDO($dsn, Config::DB_USER, Config::DB_PASSWORD);

            // Throw an Exception when an error occurs
            $db->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
        }

        return $db;
    }

    public static function str_starts_with ( $haystack, $needle ) {
        return strpos( $haystack , $needle ) === 0;
    }

    protected static function gld( $tbl, $slct_where = [], $updt_set = [], $insert_into = [] ) {
        //var_dump($slct_where);
        //var_dump($updt_set);
        //var_dump($insert_into);
        $r = new Resultado( 1, Resultado::NO, "No es posible realizar la operación" );

        $tmp_where = "";
        $sql = "SELECT * FROM $tbl WHERE";

        foreach( $slct_where as $llave => $valor )
            $tmp_where .= ( empty($tmp_where)?" ": " AND " ). $llave . " = '" . $valor . "'";

        $sql .= $tmp_where;

        $_db = static::getDB();
        $st = $_db -> query( $sql );
        $res = $st -> fetchAll(PDO::FETCH_ASSOC);

        $tmp_llaves  = "";
        $tmp_valores = "";
        $tmp_set     = "";

        if( empty($res) ) {
            foreach( $insert_into as $llave => $valor ) {
                $tmp_llaves .= ( empty($tmp_llaves)?" ": " , " ). $llave;
                $tmp_valores .= ( empty($tmp_valores)?" '": " , '" ). $valor . "'";
            }
            $sql = "INSERT INTO $tbl( $tmp_llaves ) VALUES ( $tmp_valores )";
        } else {
            $val = "";
            foreach( $updt_set as $llave => $valor ){
                if( static::str_starts_with( $valor, "[str]" ) ) {
                    $val = str_replace( "[str]", "", $valor );
                    $val = "'" . $val . "'";
                } else
                    $val = $valor;

                $tmp_set .= ( empty($tmp_set)?" ": " , " ). $llave . " = " . $val; 
            }
            
            $sql = "UPDATE $tbl SET $tmp_set WHERE $tmp_where";
        }

        //echo $sql; return;

        $stmt = $_db->prepare( $sql );
        $stmt->execute();

        $r->__set( "errno", 0 )->__set( "resultado", Resultado::OK );
        $r->__set( "mensaje", "Transacción operada" );
    
        return $r;
    }
}

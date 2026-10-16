<?php
namespace App\Models;

/*class Result {
	public $errores = [];

	public function __construct( $data = [] ) {
		foreach( $data as $key => $value )
	  		$this -> $key = $value;

		// foreach( $data as $nombre_campo => $valor ){
		// 	$asignacion = "\$" . $nombre_campo . "='" . $valor . "';";
		// 	eval( $asignacion );
		//}
	}
}*/

class Resultado implements \JsonSerializable {

	/**
     * Tipo de respuesta: OK
     * @var string
     */
    const OK = "OK";

    /**
     * Tipo de respuesta: NO
     * @var string
     */
    const NO = "NO";


	//private $errores = [];

	private $errno;
    private $resultado;
    private $mensaje;

	public function __construct( $errno, $resultado, $mensaje ) {
		$this->__set( "errno",		$errno );
		$this->__set( "resultado",	$resultado );
		$this->__set( "mensaje", 	$mensaje );
	}

	//Este método funciona cuando el constructor es sin parámetros
	// public static function crear() {
	// 	$instance = new self();
	// 	return $instance;
	// }
	// uso:
    //$response = Result::crear()->__set( "_errno", 1 )->__set( "_resultado", "NO" )->__set( "_mensaje", "No es posible eliminar la cita" );

	// método __getter
    public function __get( $variable ){
        if( !empty($this->$variable) ){
            $get_variable = $this->$variable;
        }

        return $get_variable;
    }

    // método __setter
    public function __set( $variable, $target ){
        $this->$variable = $target;
        return $this;
    }

    /**
     * Errno setter - fluent style
     */
 	//public function setErrno( $errno ) {
	// 	$this->_errno = $errno;
	// 	return $this;
	// }

	public function jsonSerialize() {
        $vars = get_object_vars( $this );
        return $vars;
    }
}
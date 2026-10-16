<?php
namespace App;

use App\Token;

class Auth {

	public static function getUsuario() {
		if( isset( $_SESSION[ 'codUsr' ] ) ) {
			return $_SESSION[ 'codUsr' ];
		} else {
            $tok = Token::getGuidv4();
            $_SESSION[ 'codUsr' ] = $tok;
			return $tok;
		}
	}
}
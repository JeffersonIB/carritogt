<?php

namespace App;

/**
 * Tokens, únicos, aleatorios
 *
 * PHP version 7.0
 */
class Token {
    /**
     * The token value
     * @var array
     */
    protected $token;

    /**
     * Constructor. Crea un nuevo token, aleatorio, o asigna uno existente.
     *
     * @param string $value (optional) El valor de un token
     *
     * @return void
     */
    public function __construct($token_value = null) {
        if( $token_value ) {
            $this->token = $token_value;
        } else {
            // 16 bytes = 128 bits = 32 hex characters
            $this->token = bin2hex( random_bytes( 16 ) );
        }
    }

    /**
     * Obtiene el valor del token
     *
     * @return string El valor del token
     */
    public function getValue() {
        return $this->token;
    }

    /**
     * Obtiene el valor del token convertido con la función hash
     *
     * @return string El valor del token convertido con la función hash
     */
    public function getHash() {
        return hash_hmac( 'sha256', $this->token, \App\Config::SECRET_KEY );  // sha256 = 64 chars
    }

    public static function getGuidv4() {
        if ( function_exists( 'com_create_guid' ) === true )
            return trim( com_create_guid(), '{}' );

        $data = openssl_random_pseudo_bytes(16);
        $data[ 6 ] = chr( ord($data[ 6 ] ) & 0x0f | 0x40 ); // set version to 0100
        $data[ 8 ] = chr( ord($data[ 8 ] ) & 0x3f | 0x80 ); // set bits 6-7 to 10

        return vsprintf( '%s%s-%s-%s-%s-%s%s%s', str_split(bin2hex( $data ), 4 ) );
    }
}

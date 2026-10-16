package com.carritogt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de CarritoGT.
 * Aplicacion monolitica organizada por capas: Controller -> Service -> Repository -> Entity.
 */
@SpringBootApplication
public class CarritoGtApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarritoGtApplication.class, args);
    }
}

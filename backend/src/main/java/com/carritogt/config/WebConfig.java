package com.carritogt.config;

import com.carritogt.controller.CarritoController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Capa de configuracion: CORS explicito y restringido (RNF-07).
 *
 * Si el frontend se abre desde el mismo Spring Boot (http://localhost:8080) no hace falta CORS.
 * Si se sirve desde Apache (http://carrito-compra.localhost), ese origen debe estar en
 * la propiedad app.cors.allowed-origins.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] origenesPermitidos;

    public WebConfig(@Value("${app.cors.allowed-origins:}") String[] origenes) {
        this.origenesPermitidos = Arrays.stream(origenes)
                .map(String::trim)
                .filter(origen -> !origen.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (origenesPermitidos.length == 0) {
            return;
        }
        registry.addMapping("/api/**")
                .allowedOrigins(origenesPermitidos)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", CarritoController.ENCABEZADO_USUARIO)
                .maxAge(3600);
    }
}

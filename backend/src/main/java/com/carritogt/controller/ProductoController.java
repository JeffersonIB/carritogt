package com.carritogt.controller;

import com.carritogt.dto.ProductoResponse;
import com.carritogt.service.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Capa Controller: catalogo de productos (reemplaza a Home.php).
 * Solo recibe la peticion HTTP y delega en la capa Service.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** GET /api/productos  ->  200 con la lista de productos. */
    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listar();
    }

    /** GET /api/productos/{id}  ->  200 con el producto, o 404 si no existe. */
    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Integer id) {
        return productoService.obtener(id);
    }
}

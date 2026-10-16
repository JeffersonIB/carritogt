package com.carritogt.service;

import com.carritogt.dto.ProductoResponse;
import com.carritogt.entity.Producto;
import com.carritogt.exception.RecursoNoEncontradoException;
import com.carritogt.mapper.CarritoMapper;
import com.carritogt.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Capa Service: reglas y consultas del catalogo de productos. */
@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CarritoMapper mapper;

    public ProductoService(ProductoRepository productoRepository, CarritoMapper mapper) {
        this.productoRepository = productoRepository;
        this.mapper = mapper;
    }

    /** RF-01: lista los productos disponibles en la base de datos. */
    public List<ProductoResponse> listar() {
        return productoRepository.findAllByOrderByIdProductoAsc().stream()
                .map(mapper::toProducto)
                .toList();
    }

    /** RF-02: informacion de un producto. */
    public ProductoResponse obtener(Integer idProducto) {
        return mapper.toProducto(buscarEntidad(idProducto));
    }

    /** Uso interno de la capa de servicios (por ejemplo, CarritoService). */
    public Producto buscarEntidad(Integer idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el producto con id " + idProducto));
    }
}

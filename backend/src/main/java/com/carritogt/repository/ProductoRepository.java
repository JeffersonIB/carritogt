package com.carritogt.repository;

import com.carritogt.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Capa Repository: acceso a la tabla "producto".
 * Spring Data genera la implementacion (equivale a ProductoModel::getAll() en PHP).
 */
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findAllByOrderByIdProductoAsc();
}

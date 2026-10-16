package com.carritogt.repository;

import com.carritogt.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Capa Repository: acceso a la tabla "shopping_cart".
 * Todas las consultas usan parametros (sin concatenar texto), lo que evita la
 * inyeccion SQL presente en ShoppingCartModel.php.
 */
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Integer> {

    /** Articulos del carrito de un usuario junto con su producto (una sola consulta). */
    @Query("select i from ItemCarrito i join fetch i.producto where i.codUsuario = :codUsuario order by i.idItem")
    List<ItemCarrito> buscarPorUsuario(@Param("codUsuario") String codUsuario);

    /** Busca un articulo solo si pertenece al usuario indicado. */
    Optional<ItemCarrito> findByIdItemAndCodUsuario(Integer idItem, String codUsuario);

    /** Articulo existente del mismo producto en el carrito del usuario (para acumular cantidades). */
    Optional<ItemCarrito> findFirstByCodUsuarioAndProducto_IdProducto(String codUsuario, Integer idProducto);

    /** Vacia el carrito de un usuario. */
    @Modifying
    @Query("delete from ItemCarrito i where i.codUsuario = :codUsuario")
    int eliminarPorUsuario(@Param("codUsuario") String codUsuario);
}

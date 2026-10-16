package com.carritogt.service;

import com.carritogt.dto.CarritoResponse;
import com.carritogt.entity.CodigoUsuario;
import com.carritogt.entity.ItemCarrito;
import com.carritogt.entity.Producto;
import com.carritogt.exception.RecursoNoEncontradoException;
import com.carritogt.mapper.CarritoMapper;
import com.carritogt.repository.ItemCarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Capa Service: logica del carrito migrada desde ShoppingCartModel.php.
 *
 * Diferencias de seguridad respecto a la version PHP:
 * - Un usuario solo puede ver, modificar o eliminar los articulos de SU carrito.
 * - Las cantidades se validan y los totales se calculan en el servidor.
 */
@Service
@Transactional(readOnly = true)
public class CarritoService {

    private final ItemCarritoRepository itemRepository;
    private final ProductoService productoService;
    private final CarritoMapper mapper;

    public CarritoService(ItemCarritoRepository itemRepository, ProductoService productoService,
                          CarritoMapper mapper) {
        this.itemRepository = itemRepository;
        this.productoService = productoService;
        this.mapper = mapper;
    }

    /** RF-04: contenido actual del carrito del usuario. */
    public CarritoResponse obtenerCarrito(CodigoUsuario usuario) {
        return mapper.toCarrito(itemRepository.buscarPorUsuario(usuario.valor()));
    }

    /**
     * RF-03: agrega un producto. Si ya estaba en el carrito, acumula la cantidad
     * (mismo comportamiento que "cantidad = cantidad + n" de la version PHP).
     */
    @Transactional
    public CarritoResponse agregarProducto(CodigoUsuario usuario, Integer idProducto, int cantidad) {
        Producto producto = productoService.buscarEntidad(idProducto);

        itemRepository.findFirstByCodUsuarioAndProducto_IdProducto(usuario.valor(), producto.getIdProducto())
                .ifPresentOrElse(
                        existente -> existente.aumentarCantidad(cantidad),
                        () -> itemRepository.save(new ItemCarrito(usuario.valor(), producto, cantidad)));

        return obtenerCarrito(usuario);
    }

    /** Mejora opcional: cambia la cantidad de un articulo del carrito. */
    @Transactional
    public CarritoResponse actualizarCantidad(CodigoUsuario usuario, Integer idItem, int cantidad) {
        buscarItemDelUsuario(usuario, idItem).cambiarCantidad(cantidad);
        return obtenerCarrito(usuario);
    }

    /** RF-05: elimina un articulo del carrito. */
    @Transactional
    public void eliminarItem(CodigoUsuario usuario, Integer idItem) {
        itemRepository.delete(buscarItemDelUsuario(usuario, idItem));
    }

    /** Vacia el carrito del usuario. */
    @Transactional
    public void vaciar(CodigoUsuario usuario) {
        itemRepository.eliminarPorUsuario(usuario.valor());
    }

    private ItemCarrito buscarItemDelUsuario(CodigoUsuario usuario, Integer idItem) {
        return itemRepository.findByIdItemAndCodUsuario(idItem, usuario.valor())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El articulo " + idItem + " no existe en su carrito"));
    }
}

package com.carritogt.controller;

import com.carritogt.dto.ActualizarCantidadRequest;
import com.carritogt.dto.AgregarItemRequest;
import com.carritogt.dto.CarritoResponse;
import com.carritogt.entity.CodigoUsuario;
import com.carritogt.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Capa Controller: carrito de compras (reemplaza a ShoppingCart.php).
 * El comprador se identifica con el encabezado HTTP "X-Usuario".
 */
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    public static final String ENCABEZADO_USUARIO = "X-Usuario";

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    /** GET /api/carrito  ->  200 con el contenido del carrito. */
    @GetMapping
    public CarritoResponse obtener(@RequestHeader(ENCABEZADO_USUARIO) String usuario) {
        return carritoService.obtenerCarrito(CodigoUsuario.de(usuario));
    }

    /** POST /api/carrito/items  ->  201 con el carrito actualizado. */
    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> agregar(@RequestHeader(ENCABEZADO_USUARIO) String usuario,
                                                   @Valid @RequestBody AgregarItemRequest solicitud) {
        CarritoResponse carrito = carritoService.agregarProducto(
                CodigoUsuario.de(usuario), solicitud.idProducto(), solicitud.cantidad());
        return ResponseEntity.status(HttpStatus.CREATED).body(carrito);
    }

    /** PUT /api/carrito/items/{idItem}  ->  200 con el carrito actualizado. */
    @PutMapping("/items/{idItem}")
    public CarritoResponse actualizarCantidad(@RequestHeader(ENCABEZADO_USUARIO) String usuario,
                                              @PathVariable Integer idItem,
                                              @Valid @RequestBody ActualizarCantidadRequest solicitud) {
        return carritoService.actualizarCantidad(CodigoUsuario.de(usuario), idItem, solicitud.cantidad());
    }

    /** DELETE /api/carrito/items/{idItem}  ->  204 sin contenido. */
    @DeleteMapping("/items/{idItem}")
    public ResponseEntity<Void> eliminar(@RequestHeader(ENCABEZADO_USUARIO) String usuario,
                                         @PathVariable Integer idItem) {
        carritoService.eliminarItem(CodigoUsuario.de(usuario), idItem);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/carrito  ->  204 sin contenido (vacia el carrito). */
    @DeleteMapping
    public ResponseEntity<Void> vaciar(@RequestHeader(ENCABEZADO_USUARIO) String usuario) {
        carritoService.vaciar(CodigoUsuario.de(usuario));
        return ResponseEntity.noContent().build();
    }
}

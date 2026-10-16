package com.carritogt.entity;

import com.carritogt.exception.ReglaNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Capa Entity: representa la tabla "shopping_cart" del script original cc.sql.
 * Cada fila es un articulo (producto + cantidad) dentro del carrito de un usuario.
 *
 * Encapsulamiento: la cantidad solo cambia mediante metodos que validan las reglas
 * del negocio, nunca con un setter libre.
 */
@Entity
@Table(name = "shopping_cart")
public class ItemCarrito {

    /** Cantidad maxima de unidades de un mismo producto en el carrito. */
    public static final int CANTIDAD_MAXIMA = 99;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idShoppingCart")
    private Integer idItem;

    /** Relacion N:1 con producto (clave foranea cc_profk_1 -> producto.idProducto). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idProducto")
    private Producto producto;

    /** Identificador anonimo del usuario (equivale a $_SESSION['codUsr'] en PHP). */
    @Column(name = "codUsuario", length = 128)
    private String codUsuario;

    @Column(name = "cantidad")
    private Integer cantidad;

    /** Constructor requerido por JPA. */
    protected ItemCarrito() {
    }

    public ItemCarrito(String codUsuario, Producto producto, int cantidad) {
        validarCantidad(cantidad);
        this.codUsuario = codUsuario;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    /** Suma unidades a un articulo existente (mismo comportamiento que "cantidad + n" en PHP). */
    public void aumentarCantidad(int unidades) {
        if (unidades < 1) {
            throw new ReglaNegocioException("Debe agregar al menos 1 unidad");
        }
        validarCantidad(this.cantidad + unidades);
        this.cantidad += unidades;
    }

    /** Reemplaza la cantidad (mejora: el sistema PHP no permitia modificar cantidades). */
    public void cambiarCantidad(int nuevaCantidad) {
        validarCantidad(nuevaCantidad);
        this.cantidad = nuevaCantidad;
    }

    /** Subtotal calculado en el servidor: precio x cantidad, con 2 decimales. */
    public BigDecimal getSubtotal() {
        BigDecimal precio = producto.getPrecio() == null ? BigDecimal.ZERO : producto.getPrecio();
        return precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    private static void validarCantidad(int cantidad) {
        if (cantidad < 1 || cantidad > CANTIDAD_MAXIMA) {
            throw new ReglaNegocioException(
                    "La cantidad de un producto debe estar entre 1 y " + CANTIDAD_MAXIMA);
        }
    }

    public Integer getIdItem() {
        return idItem;
    }

    public Producto getProducto() {
        return producto;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public Integer getCantidad() {
        return cantidad;
    }
}

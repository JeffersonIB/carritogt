package com.carritogt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Capa Entity: representa la tabla "producto" del script original cc.sql.
 * Equivale a ProductoModel.php. El catalogo es de solo lectura, por eso no hay setters.
 */
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProducto")
    private Integer idProducto;

    @Column(name = "nombre", length = 255)
    private String nombre;

    @Column(name = "codigo", nullable = false, length = 255)
    private String codigo;

    /** Ruta relativa de la imagen, por ejemplo: images/product/camera.jpg */
    @Column(name = "imagen")
    private String imagen;

    @Column(name = "precio")
    private BigDecimal precio;

    /** Constructor requerido por JPA. */
    protected Producto() {
    }

    public Producto(String nombre, String codigo, String imagen, BigDecimal precio) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.imagen = imagen;
        this.precio = precio;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getImagen() {
        return imagen;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}

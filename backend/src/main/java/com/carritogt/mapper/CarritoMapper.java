package com.carritogt.mapper;

import com.carritogt.dto.CarritoResponse;
import com.carritogt.dto.ItemCarritoResponse;
import com.carritogt.dto.ProductoResponse;
import com.carritogt.entity.ItemCarrito;
import com.carritogt.entity.Producto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Convierte entidades JPA en DTO. Asi la API nunca expone las entidades directamente
 * y el formato JSON puede evolucionar sin tocar la base de datos.
 */
@Component
public class CarritoMapper {

    public ProductoResponse toProducto(Producto p) {
        return new ProductoResponse(p.getIdProducto(), p.getNombre(), p.getCodigo(),
                p.getImagen(), dosDecimales(p.getPrecio()));
    }

    public ItemCarritoResponse toItem(ItemCarrito item) {
        Producto p = item.getProducto();
        return new ItemCarritoResponse(item.getIdItem(), p.getIdProducto(), p.getNombre(), p.getCodigo(),
                p.getImagen(), dosDecimales(p.getPrecio()), item.getCantidad(), item.getSubtotal());
    }

    /** El total y la cantidad de articulos se calculan aqui, nunca se confia en el navegador. */
    public CarritoResponse toCarrito(List<ItemCarrito> items) {
        List<ItemCarritoResponse> detalle = items.stream().map(this::toItem).toList();
        int totalArticulos = items.stream().mapToInt(ItemCarrito::getCantidad).sum();
        BigDecimal total = items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return new CarritoResponse(detalle, totalArticulos, total);
    }

    private static BigDecimal dosDecimales(BigDecimal valor) {
        return valor == null ? null : valor.setScale(2, RoundingMode.HALF_UP);
    }
}

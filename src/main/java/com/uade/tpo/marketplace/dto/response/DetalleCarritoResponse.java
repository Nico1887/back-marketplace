package com.uade.tpo.marketplace.dto.response;

import com.uade.tpo.marketplace.entity.DetalleCarrito;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DetalleCarritoResponse {

    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public static DetalleCarritoResponse from(DetalleCarrito detalle) {
        return new DetalleCarritoResponse(
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getProducto().getPrecio(),
                detalle.calcularSubtotal()
        );
    }
}
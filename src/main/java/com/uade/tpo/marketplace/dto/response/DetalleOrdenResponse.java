package com.uade.tpo.marketplace.dto.response;

import com.uade.tpo.marketplace.entity.DetalleOrden;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DetalleOrdenResponse {

    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public static DetalleOrdenResponse from(DetalleOrden detalle) {
        return new DetalleOrdenResponse(
                detalle.getProducto().getId(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.calcularSubtotal()
        );
    }
}

package com.uade.tpo.marketplace.dto.response;

import com.uade.tpo.marketplace.entity.Carrito;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CarritoResponse {

    private Long id;
    private List<DetalleCarritoResponse> detalles;
    private BigDecimal total;

    public static CarritoResponse from(Carrito carrito) {
        List<DetalleCarritoResponse> detalles = carrito.getDetalles().stream()
                .map(DetalleCarritoResponse::from)
                .toList();

        return new CarritoResponse(carrito.getId(), detalles, carrito.calcularTotal());
    }
}
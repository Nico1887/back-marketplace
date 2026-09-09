package com.uade.tpo.marketplace.dto.response;

import com.uade.tpo.marketplace.entity.OrdenCompra;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrdenResponse {

    private Long id;
    private LocalDateTime fecha;
    private BigDecimal total;
    private List<DetalleOrdenResponse> detalles;

    public static OrdenResponse from(OrdenCompra orden) {
        List<DetalleOrdenResponse> detalles = orden.getDetalles().stream()
                .map(DetalleOrdenResponse::from)
                .toList();
        return new OrdenResponse(orden.getId(), orden.getFecha(), orden.getTotal(), detalles);
    }
}

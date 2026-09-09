package com.uade.tpo.marketplace.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoListadoResponse {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String imagenPortada;
}

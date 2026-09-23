package com.uade.tpo.marketplace.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private boolean activo;
    private String nombreVendedor;
    private List<String> categorias;
    private List<String> imagenes;
    private boolean disponibleParaCarrito;
}
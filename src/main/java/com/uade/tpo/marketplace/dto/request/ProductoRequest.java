package com.uade.tpo.marketplace.dto.request;


import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductoRequest {

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Long vendedorId;
    private List<Long> categoriaIds;
    private List<ImagenRequest> imagenes; 

}

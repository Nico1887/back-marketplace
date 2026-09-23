package com.uade.tpo.marketplace.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductoRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @NotBlank
    @Size(max = 2000)
    private String descripcion;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precio;

    @NotNull
    @Positive
    private Integer stock;

    private List<Long> categoriaIds;

    private List<@Valid ImagenRequest> imagenes;

}

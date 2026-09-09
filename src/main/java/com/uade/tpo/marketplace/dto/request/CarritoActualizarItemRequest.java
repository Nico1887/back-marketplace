package com.uade.tpo.marketplace.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CarritoActualizarItemRequest {

    @NotNull
    @Positive
    private Integer cantidad;
}
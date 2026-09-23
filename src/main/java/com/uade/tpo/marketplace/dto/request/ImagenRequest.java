package com.uade.tpo.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ImagenRequest {

    @NotBlank
    @Size(max = 500)
    private String urlImagen;
}

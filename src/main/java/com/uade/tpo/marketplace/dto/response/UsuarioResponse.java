package com.uade.tpo.marketplace.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class UsuarioResponse {
    private Long id;
    private String username;
    private String nombre;
    private String apellido;
    private String email;
    private List<String> roles;
}

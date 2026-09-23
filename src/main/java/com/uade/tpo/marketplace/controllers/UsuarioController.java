package com.uade.tpo.marketplace.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.dto.request.UsuarioActualizarRequest;
import com.uade.tpo.marketplace.dto.response.UsuarioResponse;
import com.uade.tpo.marketplace.security.UsuarioDetailsImpl;
import com.uade.tpo.marketplace.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioActualizarRequest request,
            Authentication authentication) {
        UsuarioDetailsImpl usuarioAutenticado = (UsuarioDetailsImpl) authentication.getPrincipal();
        UsuarioResponse response = usuarioService.actualizarPerfil(id, request, usuarioAutenticado.getId());
        return ResponseEntity.ok(response);
    }
}

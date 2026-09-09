package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.dto.request.CarritoAgregarItemRequest;
import com.uade.tpo.marketplace.dto.request.CarritoActualizarItemRequest;
import com.uade.tpo.marketplace.dto.response.CarritoResponse;
import com.uade.tpo.marketplace.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<CarritoResponse> obtenerOCrear(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerOCrearCarrito(usuarioId));
    }

    @PostMapping("/usuarios/{usuarioId}/items")
    public ResponseEntity<CarritoResponse> agregarItem(
            @PathVariable Long usuarioId,
            @Valid @RequestBody CarritoAgregarItemRequest request) {
        return ResponseEntity.ok(carritoService.agregarItem(usuarioId, request));
    }

    @PutMapping("/usuarios/{usuarioId}/items/{productoId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId,
            @Valid @RequestBody CarritoActualizarItemRequest request) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(usuarioId, productoId, request));
    }

    @DeleteMapping("/usuarios/{usuarioId}/items/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId) {
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, productoId));
    }

    @DeleteMapping("/usuarios/{usuarioId}/items")
    public ResponseEntity<CarritoResponse> vaciarCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(usuarioId));
    }
}
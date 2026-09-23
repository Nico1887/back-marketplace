package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.dto.request.ProductoRequest;
import com.uade.tpo.marketplace.dto.response.ProductoResponse;
import com.uade.tpo.marketplace.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uade.tpo.marketplace.dto.request.ImagenRequest;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    // Id del usuario logueado (el que devuelve /auth/login). Identifica al vendedor que hace la operacion.
    private static final String HEADER_USUARIO = "X-Usuario-Id";

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestHeader(value = HEADER_USUARIO, required = false) Long usuarioId,
                                                  @Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crearProducto(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> modificar(@PathVariable Long id,
                                                        @RequestHeader(value = HEADER_USUARIO, required = false) Long usuarioId,
                                                        @Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.modificarProducto(id, request, usuarioId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Long id,
                                          @RequestHeader(value = HEADER_USUARIO, required = false) Long usuarioId) {
        productoService.darDeBajaProducto(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoResponse> actualizarStock(@PathVariable Long id,
                                                               @RequestHeader(value = HEADER_USUARIO, required = false) Long usuarioId,
                                                               @RequestParam Integer cantidad) {
        ProductoResponse response = productoService.actualizarStock(id, cantidad, usuarioId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/imagenes")
    public ResponseEntity<ProductoResponse> agregarImagen(@PathVariable Long id,
                                                          @RequestHeader(value = HEADER_USUARIO, required = false) Long usuarioId,
                                                          @Valid @RequestBody ImagenRequest request) {
        ProductoResponse response = productoService.agregarImagen(id, request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/imagenes/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long id,
                                               @PathVariable Long imagenId,
                                               @RequestHeader(value = HEADER_USUARIO, required = false) Long usuarioId) {
        productoService.eliminarImagen(id, imagenId, usuarioId);
        return ResponseEntity.noContent().build();
    }

}

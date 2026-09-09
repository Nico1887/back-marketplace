package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.dto.request.ProductoRequest;
import com.uade.tpo.marketplace.dto.response.ProductoResponse;
import com.uade.tpo.marketplace.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uade.tpo.marketplace.dto.request.ImagenRequest;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> modificar(@PathVariable Long id,
                                                        @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.modificarProducto(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Long id) {
        productoService.darDeBajaProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoResponse> actualizarStock(@PathVariable Long id,
                                                               @RequestParam Integer cantidad) {
        ProductoResponse response = productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok(response);
    }

@PostMapping("/{id}/imagenes")
public ResponseEntity<ProductoResponse> agregarImagen(@PathVariable Long id,
                                                          @RequestBody ImagenRequest request) {
    ProductoResponse response = productoService.agregarImagen(id, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

@DeleteMapping("/{id}/imagenes/{imagenId}")
public ResponseEntity<Void> eliminarImagen(@PathVariable Long id, @PathVariable Long imagenId) {
    productoService.eliminarImagen(id, imagenId);
    return ResponseEntity.noContent().build();
}

}
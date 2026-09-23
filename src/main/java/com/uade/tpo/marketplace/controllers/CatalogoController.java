package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.dto.response.CategoriaResponse;
import com.uade.tpo.marketplace.dto.response.ProductoListadoResponse;
import com.uade.tpo.marketplace.dto.response.ProductoResponse;
import com.uade.tpo.marketplace.service.CategoriaService;
import com.uade.tpo.marketplace.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public CatalogoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/productos")
    public ResponseEntity<Page<ProductoListadoResponse>> getCatalogo(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.getProductosCatalogo(nombre, categoriaId, pageable));
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoResponse> getProductoById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResponse>> getCategorias() {
        return ResponseEntity.ok(categoriaService.getCategorias());
    }
}

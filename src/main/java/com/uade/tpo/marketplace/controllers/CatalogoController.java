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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Catálogo", description = "Endpoints para consultar productos y categorías")
public class CatalogoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public CatalogoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Obtener catálogo de productos", description = "Devuelve un listado paginado de productos, con opción de filtrar por nombre, categoría y rango de precios. Los resultados se ordenan alfabéticamente por nombre.")
    @GetMapping("/productos")
    public ResponseEntity<Page<ProductoListadoResponse>> getCatalogo(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) java.math.BigDecimal precioMin,
            @RequestParam(required = false) java.math.BigDecimal precioMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.getProductosCatalogo(nombre, categoriaId, precioMin, precioMax, pageable));
    }

    @Operation(summary = "Obtener detalle de producto", description = "Devuelve el detalle completo de un producto específico, incluyendo sus imágenes, categorías y si hay stock disponible para el carrito.")
    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoResponse> getProductoById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    @Operation(summary = "Obtener categorías", description = "Devuelve un listado de todas las categorías disponibles para filtrar productos.")
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResponse>> getCategorias() {
        return ResponseEntity.ok(categoriaService.getCategorias());
    }
}

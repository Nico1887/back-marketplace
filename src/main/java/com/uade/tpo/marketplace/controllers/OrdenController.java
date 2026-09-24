package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.dto.response.OrdenResponse;
import com.uade.tpo.marketplace.service.OrdenCompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Ordenes", description = "Endpoints para el checkout y visualización de órdenes de compra")
public class OrdenController {

    private final OrdenCompraService ordenCompraService;

    public OrdenController(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;
    }

    @Operation(summary = "Realizar checkout", description = "Crea una orden de compra a partir del carrito del usuario, descontando el stock y vaciando el carrito.")
    @PostMapping("/carrito/usuarios/{usuarioId}/checkout")
    public ResponseEntity<OrdenResponse> checkout(@PathVariable Long usuarioId) {
        OrdenResponse orden = ordenCompraService.checkout(usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    @Operation(summary = "Obtener historial de órdenes", description = "Devuelve el historial de órdenes de compra del usuario ordenado por fecha de forma descendente.")
    @GetMapping("/ordenes/usuarios/{usuarioId}")
    public ResponseEntity<List<OrdenResponse>> getOrdenesByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenCompraService.getOrdenesByUsuario(usuarioId));
    }
}

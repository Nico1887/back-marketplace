package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.*;
import com.uade.tpo.marketplace.repository.CarritoRepository;
import com.uade.tpo.marketplace.repository.OrdenCompraRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.uade.tpo.marketplace.dto.response.OrdenResponse;

@Service
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;
    private final CarritoService carritoService;

    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository,
                              CarritoRepository carritoRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoService productoService,
                              CarritoService carritoService) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoService = productoService;
        this.carritoService = carritoService;
    }

    @Transactional
    public OrdenResponse checkout(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        if (carrito.getDetalles().isEmpty()) {
            throw new RuntimeException("El carrito esta vacio");
        }

        OrdenCompra orden = new OrdenCompra(usuario);

        for (DetalleCarrito detalleCarrito : carrito.getDetalles()) {
            Producto producto = detalleCarrito.getProducto();
            Integer cantidad = detalleCarrito.getCantidad();

            // 1. Valida stock y descuenta (llamando a descontarStock de Ramiro)
            productoService.descontarStock(producto.getId(), cantidad);

            // 2. Congela precio unitario
            DetalleOrden detalleOrden = new DetalleOrden(producto, cantidad);
            orden.agregarDetalle(detalleOrden);
        }

        orden.recalcularTotal();
        OrdenCompra ordenGuardada = ordenCompraRepository.save(orden);

        // 3. Vacia carrito (llamando a vaciarCarrito de Tobias)
        carritoService.vaciarCarrito(usuarioId);

        return OrdenResponse.from(ordenGuardada);
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> getOrdenesByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ordenCompraRepository.findByUsuarioOrderByFechaDesc(usuario).stream()
                .map(OrdenResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrdenResponse getOrdenById(Long usuarioId, Long ordenId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        OrdenCompra orden = ordenCompraRepository.findByUsuarioAndId(usuario, ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        return OrdenResponse.from(orden);
    }
}

package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.dto.request.CarritoAgregarItemRequest;
import com.uade.tpo.marketplace.dto.request.CarritoActualizarItemRequest;
import com.uade.tpo.marketplace.dto.response.CarritoResponse;
import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.DetalleCarrito;
import com.uade.tpo.marketplace.entity.Producto;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exceptions.ResourceNotFoundException;
import com.uade.tpo.marketplace.exceptions.StateConflictException;
import com.uade.tpo.marketplace.repository.CarritoRepository;
import com.uade.tpo.marketplace.repository.DetalleCarritoRepository;
import com.uade.tpo.marketplace.repository.ProductoRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final DetalleCarritoRepository detalleCarritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoService(CarritoRepository carritoRepository,
                           DetalleCarritoRepository detalleCarritoRepository,
                           ProductoRepository productoRepository,
                           UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.detalleCarritoRepository = detalleCarritoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CarritoResponse obtenerOCrearCarrito(Long usuarioId) {
        return CarritoResponse.from(obtenerOCrearCarritoEntity(usuarioId));
    }

    @Transactional
    public CarritoResponse agregarItem(Long usuarioId, CarritoAgregarItemRequest request) {
        Carrito carrito = obtenerOCrearCarritoEntity(usuarioId);
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + request.getProductoId()));

        DetalleCarrito detalle = detalleCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
                .orElse(null);
        int cantidadActual = detalle == null ? 0 : detalle.getCantidad();
        int cantidadFinal = cantidadActual + request.getCantidad();

        if (!producto.hayStock(cantidadFinal)) {
            throw new StateConflictException("Stock insuficiente para el producto " + producto.getId());
        }

        if (detalle == null) {
            detalle = new DetalleCarrito(producto, request.getCantidad());
            carrito.agregarDetalle(detalle);
        } else {
            detalle.setCantidad(cantidadFinal);
        }

        detalleCarritoRepository.save(detalle);
        return CarritoResponse.from(carrito);
    }

    @Transactional
    public CarritoResponse actualizarCantidad(Long usuarioId, Long productoId,
                                               CarritoActualizarItemRequest request) {
        Carrito carrito = obtenerCarritoExistente(usuarioId);
        DetalleCarrito detalle = obtenerDetalle(carrito, productoId);

        if (!detalle.getProducto().hayStock(request.getCantidad())) {
            throw new StateConflictException("Stock insuficiente para el producto " + productoId);
        }

        detalle.setCantidad(request.getCantidad());
        detalleCarritoRepository.save(detalle);
        return CarritoResponse.from(carrito);
    }

    @Transactional
    public CarritoResponse eliminarItem(Long usuarioId, Long productoId) {
        Carrito carrito = obtenerCarritoExistente(usuarioId);
        DetalleCarrito detalle = obtenerDetalle(carrito, productoId);

        carrito.getDetalles().remove(detalle);
        detalleCarritoRepository.delete(detalle);
        return CarritoResponse.from(carrito);
    }

    @Transactional
    public CarritoResponse vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerCarritoExistente(usuarioId);
        carrito.vaciar();
        return CarritoResponse.from(carrito);
    }

    private Carrito obtenerCarritoExistente(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado para el usuario: " + usuarioId));
    }

    private DetalleCarrito obtenerDetalle(Carrito carrito, Long productoId) {
        return detalleCarritoRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en el carrito: " + productoId));
    }

    private Carrito obtenerOCrearCarritoEntity(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
                    return carritoRepository.save(new Carrito(usuario));
                });
    }
}
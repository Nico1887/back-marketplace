package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.DetalleCarrito;
import com.uade.tpo.marketplace.entity.Producto;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.repository.CarritoRepository;
import com.uade.tpo.marketplace.repository.OrdenCompraRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdenCompraServiceTest {

    @Mock
    private OrdenCompraRepository ordenCompraRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private OrdenCompraService ordenCompraService;

    @Test
    void checkout_FaltaStockEnUnItem_LanzaExcepcionYNoVaciaCarrito() {
        // Arrange
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Carrito carrito = new Carrito(usuario);

        // Producto 1 con stock suficiente
        Producto p1 = new Producto();
        p1.setId(10L);
        p1.setPrecio(BigDecimal.valueOf(100));
        DetalleCarrito d1 = new DetalleCarrito(p1, 2);
        carrito.agregarDetalle(d1);

        // Producto 2 sin stock suficiente
        Producto p2 = new Producto();
        p2.setId(20L);
        p2.setPrecio(BigDecimal.valueOf(200));
        DetalleCarrito d2 = new DetalleCarrito(p2, 5);
        carrito.agregarDetalle(d2);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(carrito));

        // Simular que el primer producto descuenta stock bien
        doNothing().when(productoService).descontarStock(10L, 2);
        // Simular que el segundo producto tira excepción por falta de stock
        doThrow(new RuntimeException("Stock insuficiente para el producto 20"))
                .when(productoService).descontarStock(20L, 5);

        // Act & Assert
        assertThatThrownBy(() -> ordenCompraService.checkout(usuarioId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente para el producto 20");

        // Verify: Nunca se guarda la orden de compra ni se vacía el carrito
        verify(ordenCompraRepository, never()).save(any());
        verify(carritoService, never()).vaciarCarrito(anyLong());
    }
}

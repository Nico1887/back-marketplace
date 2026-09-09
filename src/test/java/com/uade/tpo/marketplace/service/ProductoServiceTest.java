package com.uade.tpo.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uade.tpo.marketplace.dto.response.ProductoResponse;
import com.uade.tpo.marketplace.entity.Producto;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.repository.ProductoRepository;
import com.uade.tpo.marketplace.repository.CategoriaRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void getProductosCatalogo_DebeRetornarProductosPaginados() {
        Usuario vendedor = new Usuario();
        vendedor.setNombre("Test Vendedor");

        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setNombre("Zapato");
        p1.setPrecio(java.math.BigDecimal.valueOf(1000));
        p1.setActivo(true);
        p1.setVendedor(vendedor);

        Producto p2 = new Producto();
        p2.setId(2L);
        p2.setNombre("Auriculares");
        p2.setPrecio(java.math.BigDecimal.valueOf(500));
        p2.setActivo(true);
        p2.setVendedor(vendedor);

        org.springframework.data.domain.Page<Producto> page = new org.springframework.data.domain.PageImpl<>(Arrays.asList(p2, p1));

        // Se supone que el repositorio devuelve los productos ordenados
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        when(productoRepository.findCatalogo(null, null, pageable)).thenReturn(page);

        org.springframework.data.domain.Page<com.uade.tpo.marketplace.dto.response.ProductoListadoResponse> response = 
                productoService.getProductosCatalogo(null, null, pageable);

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).getNombre()).isEqualTo("Auriculares");
        assertThat(response.getContent().get(1).getNombre()).isEqualTo("Zapato");
    }

    @Test
    void getProductoById_DebeRetornarDetalleDelProducto() {
        Usuario vendedor = new Usuario();
        vendedor.setNombre("Test Vendedor");

        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Celular");
        p.setStock(10);
        p.setActivo(true);
        p.setVendedor(vendedor);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        ProductoResponse response = productoService.getProductoById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Celular");
    }
    
    @Test
    void getProductoById_ProductoNoExistente_LanzaException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.getProductoById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Producto no encontrado");
    }
}

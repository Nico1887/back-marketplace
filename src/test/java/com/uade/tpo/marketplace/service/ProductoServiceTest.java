package com.uade.tpo.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uade.tpo.marketplace.dto.request.ImagenRequest;
import com.uade.tpo.marketplace.dto.request.ProductoRequest;
import com.uade.tpo.marketplace.dto.response.ProductoResponse;
import com.uade.tpo.marketplace.entity.Categoria;
import com.uade.tpo.marketplace.entity.Producto;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exceptions.BusinessRulesException;
import com.uade.tpo.marketplace.exceptions.ForbiddenException;
import com.uade.tpo.marketplace.exceptions.InvalidCredentialsException;
import com.uade.tpo.marketplace.exceptions.ResourceNotFoundException;
import com.uade.tpo.marketplace.exceptions.StateConflictException;
import com.uade.tpo.marketplace.repository.ProductoRepository;
import com.uade.tpo.marketplace.repository.CategoriaRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;

import java.math.BigDecimal;
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

    // ---------- Datos de prueba ----------

    private static final Long VENDEDOR_ID = 1L;
    private static final Long OTRO_USUARIO_ID = 2L;

    private Usuario vendedor() {
        Usuario vendedor = new Usuario();
        vendedor.setId(VENDEDOR_ID);
        vendedor.setNombre("Juan");
        return vendedor;
    }

    private Producto productoDelVendedor(int stock) {
        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Auriculares");
        producto.setDescripcion("Bluetooth");
        producto.setPrecio(new BigDecimal("1000.00"));
        producto.setStock(stock);
        producto.setVendedor(vendedor());
        return producto;
    }

    private ProductoRequest requestValido() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Monitor");
        request.setDescripcion("Monitor 144hz");
        request.setPrecio(new BigDecimal("250000.00"));
        request.setStock(5);
        request.setCategoriaIds(List.of(1L));
        ImagenRequest imagen = new ImagenRequest();
        imagen.setUrlImagen("https://placehold.co/600x400");
        request.setImagenes(List.of(imagen));
        return request;
    }

    // ---------- Alta ----------

    @Test
    void crearProducto_ConUsuarioAutenticado_LoAsignaComoVendedor() {
        Usuario vendedor = vendedor();
        when(usuarioRepository.findById(VENDEDOR_ID)).thenReturn(Optional.of(vendedor));
        when(categoriaRepository.findAllById(List.of(1L))).thenReturn(List.of(new Categoria("Electronica")));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoResponse response = productoService.crearProducto(requestValido(), VENDEDOR_ID);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertThat(captor.getValue().getVendedor()).isSameAs(vendedor);

        assertThat(response.getNombre()).isEqualTo("Monitor");
        assertThat(response.getStock()).isEqualTo(5);
        assertThat(response.isActivo()).isTrue();
        assertThat(response.getNombreVendedor()).isEqualTo("Juan");
        assertThat(response.getCategorias()).containsExactly("Electronica");
        assertThat(response.getImagenes()).containsExactly("https://placehold.co/600x400");
    }

    @Test
    void crearProducto_SinCategoriasNiImagenes_SeCreaIgual() {
        when(usuarioRepository.findById(VENDEDOR_ID)).thenReturn(Optional.of(vendedor()));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ProductoRequest request = requestValido();
        request.setCategoriaIds(null);
        request.setImagenes(null);

        ProductoResponse response = productoService.crearProducto(request, VENDEDOR_ID);

        assertThat(response.getCategorias()).isEmpty();
        assertThat(response.getImagenes()).isEmpty();
        verifyNoInteractions(categoriaRepository);
    }

    @Test
    void crearProducto_SinUsuarioAutenticado_Lanza401() {
        assertThatThrownBy(() -> productoService.crearProducto(requestValido(), null))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(usuarioRepository, productoRepository);
    }

    @Test
    void crearProducto_UsuarioAutenticadoInexistente_Lanza401() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.crearProducto(requestValido(), 99L))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(productoRepository, never()).save(any());
    }

    // ---------- Modificacion ----------

    @Test
    void modificarProducto_PorSuVendedor_ActualizaLosDatos() {
        Producto producto = productoDelVendedor(3);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findAllById(List.of(1L))).thenReturn(List.of(new Categoria("Electronica")));
        when(productoRepository.save(producto)).thenReturn(producto);

        ProductoResponse response = productoService.modificarProducto(10L, requestValido(), VENDEDOR_ID);

        assertThat(response.getNombre()).isEqualTo("Monitor");
        assertThat(response.getDescripcion()).isEqualTo("Monitor 144hz");
        assertThat(response.getPrecio()).isEqualByComparingTo("250000.00");
        assertThat(response.getStock()).isEqualTo(5);
        assertThat(response.getCategorias()).containsExactly("Electronica");
    }

    @Test
    void modificarProducto_PorOtroUsuario_Lanza403YNoGuarda() {
        Producto producto = productoDelVendedor(3);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> productoService.modificarProducto(10L, requestValido(), OTRO_USUARIO_ID))
                .isInstanceOf(ForbiddenException.class);

        verify(productoRepository, never()).save(any());
        assertThat(producto.getNombre()).isEqualTo("Auriculares");
    }

    @Test
    void modificarProducto_SinUsuarioAutenticado_Lanza401() {
        assertThatThrownBy(() -> productoService.modificarProducto(10L, requestValido(), null))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(productoRepository);
    }

    @Test
    void modificarProducto_Inexistente_Lanza404() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.modificarProducto(99L, requestValido(), VENDEDOR_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Producto no encontrado");
    }

    // ---------- Baja logica ----------

    @Test
    void darDeBajaProducto_PorSuVendedor_LoMarcaInactivoSinBorrarlo() {
        Producto producto = productoDelVendedor(3);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        productoService.darDeBajaProducto(10L, VENDEDOR_ID);

        assertThat(producto.isActivo()).isFalse();
        verify(productoRepository).save(producto);
        verify(productoRepository, never()).delete(any());
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void darDeBajaProducto_PorOtroUsuario_Lanza403YSigueActivo() {
        Producto producto = productoDelVendedor(3);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> productoService.darDeBajaProducto(10L, OTRO_USUARIO_ID))
                .isInstanceOf(ForbiddenException.class);

        assertThat(producto.isActivo()).isTrue();
        verify(productoRepository, never()).save(any());
    }

    @Test
    void darDeBajaProducto_SinUsuarioAutenticado_Lanza401() {
        assertThatThrownBy(() -> productoService.darDeBajaProducto(10L, null))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(productoRepository);
    }

    @Test
    void darDeBajaProducto_Inexistente_Lanza404() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.darDeBajaProducto(99L, VENDEDOR_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- Descontar stock ----------

    @Test
    void descontarStock_ConStockSuficiente_DescuentaLaCantidad() {
        Producto producto = productoDelVendedor(10);
        when(productoRepository.findByIdParaActualizarStock(10L)).thenReturn(Optional.of(producto));

        productoService.descontarStock(10L, 3);

        assertThat(producto.getStock()).isEqualTo(7);
        verify(productoRepository).save(producto);
    }

    @Test
    void descontarStock_TodoElStockDisponible_QuedaEnCero() {
        Producto producto = productoDelVendedor(4);
        when(productoRepository.findByIdParaActualizarStock(10L)).thenReturn(Optional.of(producto));

        productoService.descontarStock(10L, 4);

        assertThat(producto.getStock()).isZero();
    }

    @Test
    void descontarStock_StockInsuficiente_Lanza409YNoModificaElStock() {
        Producto producto = productoDelVendedor(2);
        when(productoRepository.findByIdParaActualizarStock(10L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> productoService.descontarStock(10L, 3))
                .isInstanceOf(StateConflictException.class)
                .hasMessageContaining("Stock insuficiente");

        assertThat(producto.getStock()).isEqualTo(2);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void descontarStock_ProductoDadoDeBaja_Lanza409() {
        Producto producto = productoDelVendedor(10);
        producto.setActivo(false);
        when(productoRepository.findByIdParaActualizarStock(10L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> productoService.descontarStock(10L, 1))
                .isInstanceOf(StateConflictException.class);

        assertThat(producto.getStock()).isEqualTo(10);
    }

    @Test
    void descontarStock_ProductoInexistente_Lanza404() {
        when(productoRepository.findByIdParaActualizarStock(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.descontarStock(99L, 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -5})
    void descontarStock_CantidadInvalida_Lanza400(Integer cantidad) {
        assertThatThrownBy(() -> productoService.descontarStock(10L, cantidad))
                .isInstanceOf(BusinessRulesException.class);

        verifyNoInteractions(productoRepository);
    }
}

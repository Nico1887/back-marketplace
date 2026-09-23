package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.dto.request.ImagenRequest;
import com.uade.tpo.marketplace.dto.request.ProductoRequest;
import com.uade.tpo.marketplace.dto.response.ProductoResponse;
import com.uade.tpo.marketplace.entity.Categoria;
import com.uade.tpo.marketplace.entity.ImagenProducto;
import com.uade.tpo.marketplace.entity.Producto;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exceptions.BusinessRulesException;
import com.uade.tpo.marketplace.exceptions.ForbiddenException;
import com.uade.tpo.marketplace.exceptions.InvalidCredentialsException;
import com.uade.tpo.marketplace.exceptions.ResourceNotFoundException;
import com.uade.tpo.marketplace.exceptions.StateConflictException;
import com.uade.tpo.marketplace.repository.CategoriaRepository;
import com.uade.tpo.marketplace.repository.ProductoRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.uade.tpo.marketplace.dto.response.ProductoListadoResponse;

@Service
public class ProductoService {

    private static final String PRODUCTO_NO_ENCONTRADO = "Producto no encontrado";

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public ProductoService(ProductoRepository productoRepository,
                            CategoriaRepository categoriaRepository,
                            UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // El vendedor del producto es siempre el usuario autenticado, nunca un id que venga en el body.
    @Transactional
    public ProductoResponse crearProducto(ProductoRequest request, Long usuarioAutenticadoId) {

        Usuario vendedor = buscarUsuarioAutenticado(usuarioAutenticadoId);

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setVendedor(vendedor);
        producto.setCategorias(buscarCategorias(request.getCategoriaIds()));

        if (request.getImagenes() != null) {
            for (ImagenRequest imgReq : request.getImagenes()) {
                producto.agregarImagen(new ImagenProducto(imgReq.getUrlImagen()));
            }
        }

        Producto guardado = productoRepository.save(producto);

        return convertirAResponse(guardado);
    }

    @Transactional
    public ProductoResponse modificarProducto(Long id, ProductoRequest request, Long usuarioAutenticadoId) {

        Producto producto = buscarProductoDelVendedor(id, usuarioAutenticadoId);

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategorias(buscarCategorias(request.getCategoriaIds()));

        Producto actualizado = productoRepository.save(producto);

        return convertirAResponse(actualizado);
    }

    // Baja logica: el producto no se borra, solo se marca inactivo para no romper ordenes viejas.
    @Transactional
    public void darDeBajaProducto(Long id, Long usuarioAutenticadoId) {

        Producto producto = buscarProductoDelVendedor(id, usuarioAutenticadoId);

        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional
    public ProductoResponse actualizarStock(Long id, Integer nuevoStock, Long usuarioAutenticadoId) {

        if (nuevoStock == null || nuevoStock < 0) {
            throw new BusinessRulesException("El stock no puede ser negativo");
        }

        Producto producto = buscarProductoDelVendedor(id, usuarioAutenticadoId);

        producto.setStock(nuevoStock);
        Producto actualizado = productoRepository.save(producto);

        return convertirAResponse(actualizado);
    }

    /**
     * Descuenta stock de un producto. Es un metodo interno (no tiene endpoint) pensado para
     * que el checkout lo reutilice en lugar de reimplementar la logica de stock.
     *
     * Se une a la transaccion de quien lo llama: si el checkout falla despues de descontar,
     * el descuento se revierte junto con el resto de la compra.
     */
    @Transactional
    public void descontarStock(Long productoId, Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {
            throw new BusinessRulesException("La cantidad a descontar debe ser mayor a cero");
        }

        Producto producto = productoRepository.findByIdParaActualizarStock(productoId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCTO_NO_ENCONTRADO));

        if (!producto.isActivo()) {
            throw new StateConflictException("El producto " + productoId + " no esta disponible para la venta");
        }
        if (!producto.hayStock(cantidad)) {
            throw new StateConflictException("Stock insuficiente para el producto " + productoId);
        }

        producto.descontarStock(cantidad);
        productoRepository.save(producto);
    }

    @Transactional
    public ProductoResponse agregarImagen(Long productoId, ImagenRequest request, Long usuarioAutenticadoId) {

        Producto producto = buscarProductoDelVendedor(productoId, usuarioAutenticadoId);

        ImagenProducto imagen = new ImagenProducto(request.getUrlImagen());
        producto.agregarImagen(imagen);

        Producto actualizado = productoRepository.save(producto);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarImagen(Long productoId, Long imagenId, Long usuarioAutenticadoId) {

        Producto producto = buscarProductoDelVendedor(productoId, usuarioAutenticadoId);

        ImagenProducto imagen = producto.getImagenes().stream()
                .filter(img -> img.getId().equals(imagenId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada en este producto"));

        producto.quitarImagen(imagen);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Page<ProductoListadoResponse> getProductosCatalogo(String nombre, Long categoriaId, java.math.BigDecimal precioMin, java.math.BigDecimal precioMax, Pageable pageable) {
        return productoRepository.findCatalogo(nombre, categoriaId, precioMin, precioMax, pageable)
                .map(p -> {
                    ProductoListadoResponse res = new ProductoListadoResponse();
                    res.setId(p.getId());
                    res.setNombre(p.getNombre());
                    res.setPrecio(p.getPrecio());
                    if (!p.getImagenes().isEmpty()) {
                        res.setImagenPortada(p.getImagenes().iterator().next().getUrlImagen());
                    }
                    return res;
                });
    }

    @Transactional(readOnly = true)
    public ProductoResponse getProductoById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertirAResponse(producto);
    }

    private Usuario buscarUsuarioAutenticado(Long usuarioAutenticadoId) {
        if (usuarioAutenticadoId == null) {
            throw new InvalidCredentialsException("Debe iniciar sesion para realizar esta operacion");
        }
        return usuarioRepository.findById(usuarioAutenticadoId)
                .orElseThrow(() -> new InvalidCredentialsException("El usuario autenticado no existe"));
    }

    // Solo el vendedor que publico el producto puede editarlo o darlo de baja.
    private Producto buscarProductoDelVendedor(Long productoId, Long usuarioAutenticadoId) {
        if (usuarioAutenticadoId == null) {
            throw new InvalidCredentialsException("Debe iniciar sesion para realizar esta operacion");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCTO_NO_ENCONTRADO));

        if (!producto.getVendedor().getId().equals(usuarioAutenticadoId)) {
            throw new ForbiddenException("Solo el vendedor que publico el producto puede modificarlo");
        }
        return producto;
    }

    private HashSet<Categoria> buscarCategorias(List<Long> categoriaIds) {
        if (categoriaIds == null || categoriaIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(categoriaRepository.findAllById(categoriaIds));
    }

    private ProductoResponse convertirAResponse(Producto p) {
        ProductoResponse response = new ProductoResponse();
        response.setId(p.getId());
        response.setNombre(p.getNombre());
        response.setDescripcion(p.getDescripcion());
        response.setPrecio(p.getPrecio());
        response.setStock(p.getStock());
        response.setActivo(p.isActivo());
        response.setNombreVendedor(p.getVendedor().getNombre());
        response.setCategorias(p.getCategorias().stream().map(Categoria::getNombre).toList());
        response.setImagenes(p.getImagenes().stream().map(ImagenProducto::getUrlImagen).toList());
        response.setDisponibleParaCarrito(p.getStock() > 0);
        return response;
    }
}

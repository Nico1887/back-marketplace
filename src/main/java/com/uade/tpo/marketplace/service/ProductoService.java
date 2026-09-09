package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.dto.request.ImagenRequest;
import com.uade.tpo.marketplace.dto.request.ProductoRequest;
import com.uade.tpo.marketplace.dto.response.ProductoResponse;
import com.uade.tpo.marketplace.entity.Categoria;
import com.uade.tpo.marketplace.entity.ImagenProducto;
import com.uade.tpo.marketplace.entity.Producto;
import com.uade.tpo.marketplace.entity.Usuario;
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

    @Transactional
    public ProductoResponse crearProducto(ProductoRequest request) {

        Usuario vendedor = usuarioRepository.findById(request.getVendedorId())
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setVendedor(vendedor);

        List<Categoria> categorias = categoriaRepository.findAllById(request.getCategoriaIds());
        producto.setCategorias(new HashSet<>(categorias));

        for (ImagenRequest imgReq : request.getImagenes()) {
            ImagenProducto imagen = new ImagenProducto(imgReq.getUrlImagen());
            producto.agregarImagen(imagen);
        }

        Producto guardado = productoRepository.save(producto);

        return convertirAResponse(guardado);
    }


    @Transactional
public ProductoResponse modificarProducto(Long id, ProductoRequest request) {

    Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    producto.setNombre(request.getNombre());
    producto.setDescripcion(request.getDescripcion());
    producto.setPrecio(request.getPrecio());
    producto.setStock(request.getStock());

    List<Categoria> categorias = categoriaRepository.findAllById(request.getCategoriaIds());
    producto.setCategorias(new HashSet<>(categorias));

    Producto actualizado = productoRepository.save(producto);

    return convertirAResponse(actualizado);
}


    @Transactional
    public void darDeBajaProducto(Long id) {

    Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    producto.setActivo(false);
    productoRepository.save(producto);
}


    @Transactional
    public ProductoResponse actualizarStock(Long id, Integer nuevoStock) {

    Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    if (nuevoStock == null || nuevoStock < 0) {
        throw new IllegalArgumentException("El stock no puede ser negativo");
    }

    producto.setStock(nuevoStock);
    Producto actualizado = productoRepository.save(producto);

    return convertirAResponse(actualizado);
}


    @Transactional
public ProductoResponse agregarImagen(Long productoId, ImagenRequest request) {

    Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    ImagenProducto imagen = new ImagenProducto(request.getUrlImagen());
    producto.agregarImagen(imagen);

    Producto actualizado = productoRepository.save(producto);
    return convertirAResponse(actualizado);
}

@Transactional
public void eliminarImagen(Long productoId, Long imagenId) {

    Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    ImagenProducto imagen = producto.getImagenes().stream()
            .filter(img -> img.getId().equals(imagenId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Imagen no encontrada en este producto"));

    producto.quitarImagen(imagen);
    productoRepository.save(producto);
}

    @Transactional(readOnly = true)
    public Page<ProductoListadoResponse> getProductosCatalogo(String nombre, Long categoriaId, Pageable pageable) {
        return productoRepository.findCatalogo(nombre, categoriaId, pageable)
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
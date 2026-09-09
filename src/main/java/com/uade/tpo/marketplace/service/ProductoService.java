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
        return response;
    }
}
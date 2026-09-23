package com.uade.tpo.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.marketplace.entity.Producto;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();
    
    List<Producto> findByActivoTrueOrderByNombreAsc();

    List<Producto> findByVendedorId(Long vendedorId);

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:categoriaId IS NULL OR EXISTS (SELECT c FROM p.categorias c WHERE c.id = :categoriaId)) " +
           "AND (:precioMin IS NULL OR p.precio >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precio <= :precioMax) " +
           "ORDER BY p.nombre ASC")
    Page<Producto> findCatalogo(@Param("nombre") String nombre, 
                                @Param("categoriaId") Long categoriaId, 
                                @Param("precioMin") java.math.BigDecimal precioMin, 
                                @Param("precioMax") java.math.BigDecimal precioMax, 
                                Pageable pageable);

    // Bloquea la fila del producto hasta que termine la transaccion, para que dos compras
    // simultaneas no descuenten el mismo stock a la vez.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdParaActualizarStock(@Param("id") Long id);

}
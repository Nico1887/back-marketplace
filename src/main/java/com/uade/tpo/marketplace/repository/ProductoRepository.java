package com.uade.tpo.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.marketplace.entity.Producto;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();
    
    List<Producto> findByActivoTrueOrderByNombreAsc();

    List<Producto> findByVendedorId(Long vendedorId);

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:categoriaId IS NULL OR EXISTS (SELECT c FROM p.categorias c WHERE c.id = :categoriaId)) " +
           "ORDER BY p.nombre ASC")
    Page<Producto> findCatalogo(@Param("nombre") String nombre, @Param("categoriaId") Long categoriaId, Pageable pageable);

}
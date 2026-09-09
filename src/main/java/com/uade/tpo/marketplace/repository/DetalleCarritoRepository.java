package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.DetalleCarrito;
import com.uade.tpo.marketplace.entity.DetalleCarritoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, DetalleCarritoId> {

    List<DetalleCarrito> findByCarritoId(Long carritoId);

    Optional<DetalleCarrito> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
}
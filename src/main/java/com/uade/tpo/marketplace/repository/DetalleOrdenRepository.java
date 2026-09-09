package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.DetalleOrden;
import com.uade.tpo.marketplace.entity.DetalleOrdenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, DetalleOrdenId> {
}

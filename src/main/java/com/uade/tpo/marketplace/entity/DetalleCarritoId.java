package com.uade.tpo.marketplace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class DetalleCarritoId implements Serializable {

    @Column(name = "carrito_id")
    private Long carritoId;

    @Column(name = "producto_id")
    private Long productoId;

    public DetalleCarritoId(Long carritoId, Long productoId) {
        this.carritoId = carritoId;
        this.productoId = productoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleCarritoId otro)) return false;
        return Objects.equals(carritoId, otro.carritoId)
                && Objects.equals(productoId, otro.productoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carritoId, productoId);
    }
}

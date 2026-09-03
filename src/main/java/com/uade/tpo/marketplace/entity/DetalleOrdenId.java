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
public class DetalleOrdenId implements Serializable {

    @Column(name = "orden_id")
    private Long ordenId;

    @Column(name = "producto_id")
    private Long productoId;

    public DetalleOrdenId(Long ordenId, Long productoId) {
        this.ordenId = ordenId;
        this.productoId = productoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleOrdenId otro)) return false;
        return Objects.equals(ordenId, otro.ordenId)
                && Objects.equals(productoId, otro.productoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordenId, productoId);
    }
}

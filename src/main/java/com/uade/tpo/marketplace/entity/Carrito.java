package com.uade.tpo.marketplace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito", uniqueConstraints = @UniqueConstraint(name = "uk_carrito_usuario", columnNames = "usuario_id"))
@Getter
@Setter
@NoArgsConstructor
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCarrito> detalles = new ArrayList<>();

    public Carrito(Usuario usuario) {
        this.usuario = usuario;
    }

    public void agregarDetalle(DetalleCarrito detalle) {
        detalles.add(detalle);
        detalle.setCarrito(this);
    }

    public void vaciar() {
        detalles.clear();
    }

    public BigDecimal calcularTotal() {
        return detalles.stream()
                .map(DetalleCarrito::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carrito otro)) return false;
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

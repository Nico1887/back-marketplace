package com.uade.tpo.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.marketplace.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
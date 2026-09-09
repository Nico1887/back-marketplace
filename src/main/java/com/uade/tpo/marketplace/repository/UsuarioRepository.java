package com.uade.tpo.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.marketplace.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
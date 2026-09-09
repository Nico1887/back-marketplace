package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.dto.response.CategoriaResponse;
import com.uade.tpo.marketplace.entity.Categoria;
import com.uade.tpo.marketplace.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> getCategorias() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> new CategoriaResponse(categoria.getId(), categoria.getNombre()))
                .toList();
    }
}

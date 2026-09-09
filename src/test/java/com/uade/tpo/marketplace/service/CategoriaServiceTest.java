package com.uade.tpo.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uade.tpo.marketplace.dto.response.CategoriaResponse;
import com.uade.tpo.marketplace.entity.Categoria;
import com.uade.tpo.marketplace.repository.CategoriaRepository;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void getCategorias_DebeRetornarListaDeCategorias() {
        Categoria c1 = new Categoria("Electrónica");
        c1.setId(1L);
        Categoria c2 = new Categoria("Ropa");
        c2.setId(2L);

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<CategoriaResponse> response = categoriaService.getCategorias();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getNombre()).isEqualTo("Electrónica");
        assertThat(response.get(1).getNombre()).isEqualTo("Ropa");
    }
}

package com.uade.tpo.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uade.tpo.marketplace.dto.request.UsuarioActualizarRequest;
import com.uade.tpo.marketplace.dto.response.UsuarioResponse;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exceptions.ForbiddenException;
import com.uade.tpo.marketplace.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioExistente(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername("jperez");
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setEmail("jperez@uade.edu.ar");
        usuario.setPassword("hash");
        usuario.setRoles(new HashSet<>());
        return usuario;
    }

    private UsuarioActualizarRequest requestValido() {
        UsuarioActualizarRequest request = new UsuarioActualizarRequest();
        request.setUsername("jperez");
        request.setNombre("Juan");
        request.setApellido("Perez Actualizado");
        request.setEmail("jperez@uade.edu.ar");
        return request;
    }

    @Test
    void actualizarPerfilAjenoDevuelve403() {
        UsuarioActualizarRequest request = requestValido();

        assertThatThrownBy(() -> usuarioService.actualizarPerfil(2L, request, 1L))
                .isInstanceOf(ForbiddenException.class);

        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void actualizarPerfilPropioActualizaLosDatos() {
        Usuario usuario = usuarioExistente(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioActualizarRequest request = requestValido();
        request.setApellido("Perez Actualizado");

        UsuarioResponse response = usuarioService.actualizarPerfil(1L, request, 1L);

        assertThat(response.getApellido()).isEqualTo("Perez Actualizado");
    }

    @Test
    void actualizarPerfilConEmailYaUsadoDevuelve409() {
        Usuario usuario = usuarioExistente(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("otro@uade.edu.ar")).thenReturn(true);

        UsuarioActualizarRequest request = requestValido();
        request.setEmail("otro@uade.edu.ar");

        assertThatThrownBy(() -> usuarioService.actualizarPerfil(1L, request, 1L))
                .isInstanceOf(com.uade.tpo.marketplace.exceptions.StateConflictException.class);
    }
}

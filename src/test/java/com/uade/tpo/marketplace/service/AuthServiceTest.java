package com.uade.tpo.marketplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uade.tpo.marketplace.dto.request.LoginRequest;
import com.uade.tpo.marketplace.dto.request.RegistroRequest;
import com.uade.tpo.marketplace.dto.response.UsuarioResponse;
import com.uade.tpo.marketplace.entity.Rol;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exceptions.InvalidCredentialsException;
import com.uade.tpo.marketplace.exceptions.StateConflictException;
import com.uade.tpo.marketplace.repository.RolRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private AuthService authService;

    private RegistroRequest registroValido() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("jperez");
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setEmail("jperez@uade.edu.ar");
        request.setPassword("cambiar123");
        return request;
    }

    @Test
    void registrarConEmailRepetidoDevuelve409() {
        RegistroRequest request = registroValido();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(StateConflictException.class)
                .hasMessageContaining(request.getEmail());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarConUsernameRepetidoDevuelve409() {
        RegistroRequest request = registroValido();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(StateConflictException.class)
                .hasMessageContaining(request.getUsername());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarConDatosValidosGuardaElUsuarioHasheandoLaPassword() {
        RegistroRequest request = registroValido();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(rolRepository.findByNombre("ROLE_USER")).thenReturn(java.util.Optional.of(new Rol("ROLE_USER")));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        UsuarioResponse response = authService.registrar(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo(request.getUsername());
        assertThat(response.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void loginConPasswordIncorrectaDevuelve401() {
        Usuario usuario = new Usuario();
        usuario.setEmail("jperez@uade.edu.ar");
        usuario.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("cambiar123"));

        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(java.util.Optional.of(usuario));

        LoginRequest request = new LoginRequest();
        request.setEmail(usuario.getEmail());
        request.setPassword("incorrecta");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void loginConEmailInexistenteDevuelve401() {
        when(usuarioRepository.findByEmail("nadie@test.com")).thenReturn(java.util.Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setEmail("nadie@test.com");
        request.setPassword("cambiar123");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}

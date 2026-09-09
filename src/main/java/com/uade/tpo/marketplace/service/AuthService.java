package com.uade.tpo.marketplace.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.dto.request.LoginRequest;
import com.uade.tpo.marketplace.dto.request.RegistroRequest;
import com.uade.tpo.marketplace.dto.response.UsuarioResponse;
import com.uade.tpo.marketplace.entity.Rol;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exceptions.InvalidCredentialsException;
import com.uade.tpo.marketplace.exceptions.StateConflictException;
import com.uade.tpo.marketplace.repository.RolRepository;
import com.uade.tpo.marketplace.repository.UsuarioRepository;

@Service
public class AuthService {

    private static final String ROL_POR_DEFECTO = "ROLE_USER";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new StateConflictException("Ya existe un usuario registrado con el email: " + request.getEmail());
        }
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new StateConflictException("Ya existe un usuario registrado con el username: " + request.getUsername());
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        Rol rolPorDefecto = rolRepository.findByNombre(ROL_POR_DEFECTO)
                .orElseGet(() -> rolRepository.save(new Rol(ROL_POR_DEFECTO)));
        usuario.agregarRol(rolPorDefecto);

        Usuario guardado = usuarioRepository.save(usuario);
        return convertirAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email o contraseña inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new InvalidCredentialsException("Email o contraseña inválidos");
        }

        return convertirAResponse(usuario);
    }

    private UsuarioResponse convertirAResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setUsername(usuario.getUsername());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setEmail(usuario.getEmail());
        response.setRoles(usuario.getRoles().stream().map(Rol::getNombre).toList());
        return response;
    }
}

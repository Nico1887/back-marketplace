package com.uade.tpo.marketplace.exceptions;

// Este lo vamos a usar para los errores 404 (Ej: Usuario no encontrado)
public class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(String message) {
        super(message);
    }
}
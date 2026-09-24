package com.uade.tpo.marketplace.exceptions;

// Para errores 403 (Ej: un vendedor intenta modificar un producto que no es suyo)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}

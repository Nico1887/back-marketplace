package com.uade.tpo.marketplace.exceptions;

// Para errores 401 (Ej: email o password invalidos en el login)
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

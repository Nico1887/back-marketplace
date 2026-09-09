package com.uade.tpo.marketplace.exceptions;

// Para errores 409 (Ej: Estado en conflicto)
public class StateConflictException extends RuntimeException {
    public StateConflictException(String message) {
        super(message);
    }
}
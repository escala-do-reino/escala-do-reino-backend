package com.nathannolacio.escala_do_reino_backend.core.exception;

public abstract class EscalaDoReinoException extends RuntimeException {
    public EscalaDoReinoException(String message) {
        super(message);
    }
}

package com.nathannolacio.escala_do_reino_backend.domain.usuario.exception;

import com.nathannolacio.escala_do_reino_backend.core.exception.SecurityException;

public class CredenciaisInvalidasException extends SecurityException {
    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }
}

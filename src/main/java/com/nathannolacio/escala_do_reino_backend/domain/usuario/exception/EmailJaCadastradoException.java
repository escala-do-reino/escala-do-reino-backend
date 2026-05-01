package com.nathannolacio.escala_do_reino_backend.domain.usuario.exception;

import com.nathannolacio.escala_do_reino_backend.core.exception.ConflictException;

public class EmailJaCadastradoException extends ConflictException {
    public EmailJaCadastradoException(String email) {
        super("O e-mail '" + email + "' já está cadastrado no sistema.");
    }
}

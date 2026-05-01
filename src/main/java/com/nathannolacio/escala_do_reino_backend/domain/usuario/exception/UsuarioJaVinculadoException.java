package com.nathannolacio.escala_do_reino_backend.domain.usuario.exception;

import com.nathannolacio.escala_do_reino_backend.core.exception.ConflictException;

public class UsuarioJaVinculadoException extends ConflictException {
    public UsuarioJaVinculadoException() {
        super("O usuário já está vinculado a uma igreja e não pode realizar esta operação.");
    }
}

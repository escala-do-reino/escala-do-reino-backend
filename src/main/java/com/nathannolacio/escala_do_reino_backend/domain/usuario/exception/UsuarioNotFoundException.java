package com.nathannolacio.escala_do_reino_backend.domain.usuario.exception;

import com.nathannolacio.escala_do_reino_backend.core.exception.EntityNotFoundException;

public class UsuarioNotFoundException extends EntityNotFoundException {
    public UsuarioNotFoundException(Long id) {
        super("Usuário com ID " + id + " não encontrado.");
    }

    public UsuarioNotFoundException(String email) {
        super("Usuário não encontrado.");
    }
}

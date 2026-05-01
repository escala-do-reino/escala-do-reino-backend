package com.nathannolacio.escala_do_reino_backend.domain.igreja.exception;

import com.nathannolacio.escala_do_reino_backend.core.exception.EntityNotFoundException;

public class IgrejaNotFoundException extends EntityNotFoundException {
    public IgrejaNotFoundException(Long id) {
        super("Igreja com ID " + id + " não encontrada.");
    }

    public IgrejaNotFoundException(String message) {
        super(message);
    }
}

package com.nathannolacio.escala_do_reino_backend.domain.igreja.dto;

public record IgrejaCreateResponse(
        IgrejaResponse igreja,
        String token
) {
}

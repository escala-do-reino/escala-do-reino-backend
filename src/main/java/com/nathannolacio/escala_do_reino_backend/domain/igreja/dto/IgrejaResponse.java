package com.nathannolacio.escala_do_reino_backend.domain.igreja.dto;

public record IgrejaResponse(
        Long id,
        String nome,
        String setor,
        String logradouro,
        String cidade,
        String estado
) {
}

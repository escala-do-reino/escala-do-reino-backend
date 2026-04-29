package com.nathannolacio.escala_do_reino_backend.domain.usuario.dto;

public record LoginRequest(
        String email,
        String password
) {}

package com.nathannolacio.escala_do_reino_backend.domain.usuario.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) {}

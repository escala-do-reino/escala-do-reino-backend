package com.nathannolacio.escala_do_reino_backend.auth.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) {}

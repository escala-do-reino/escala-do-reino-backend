package com.nathannolacio.escala_do_reino_backend.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}

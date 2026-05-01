package com.nathannolacio.escala_do_reino_backend.core.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        LocalDateTime timestamp,
        String message,
        String path
) {
}

package com.nathannolacio.escala_do_reino_backend.domain.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(description = "Nome completo do usuário", example = "Nathan Nolacio")
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @Schema(description = "E-mail de acesso", example = "nathan@exemplo.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "123456")
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password
) {
}

package com.nathannolacio.escala_do_reino_backend.domain.igreja.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IgrejaRequest(
        @Schema(description = "Nome da igreja ou sede", example = "Assembleia de Deus")
        @NotBlank(message = "O nome da igreja é obrigatório")
        String nome,

        @Schema(description = "Logradouro (Rua, Avenida, etc.)", example = "Rua das Oliveiras")
        @NotBlank(message = "O logradouro é obrigatório")
        String logradouro,

        @Schema(description = "Número do endereço", example = "100")
        @NotBlank(message = "O número é obrigatório")
        String numero,

        @Schema(description = "Complemento (opcional)", example = "Sala 02")
        String complemento,

        @Schema(description = "Bairro", example = "Centro")
        @NotBlank(message = "O bairro é obrigatório")
        String bairro,

        @Schema(description = "Cidade", example = "São Paulo")
        @NotBlank(message = "A cidade é obrigatória")
        String cidade,

        @Schema(description = "Estado (Sigla ou nome)", example = "SP")
        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 50, message = "O estado deve ter entre 2 e 50 caracteres")
        String estado,

        @Schema(description = "CEP", example = "01001-000")
        @NotBlank(message = "O CEP é obrigatório")
        String cep
) {
}

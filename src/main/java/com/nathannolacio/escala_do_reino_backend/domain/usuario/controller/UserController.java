package com.nathannolacio.escala_do_reino_backend.domain.usuario.controller;

import com.nathannolacio.escala_do_reino_backend.core.exception.dto.ErrorResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gestão de perfil e vínculo de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Vincular usuário a uma igreja", description = "Associa o usuário autenticado a uma igreja existente e retorna um novo token JWT com o ID da igreja.")
    @ApiResponse(responseCode = "200", description = "Vínculo realizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Igreja não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Usuário já possui vínculo com uma igreja", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/vincular-igreja/{id}")
    public ResponseEntity<AuthResponse> vincularIgreja(@PathVariable Long id) {
        AuthResponse response = userService.vincularIgreja(id);
        return ResponseEntity.ok(response);
    }
}

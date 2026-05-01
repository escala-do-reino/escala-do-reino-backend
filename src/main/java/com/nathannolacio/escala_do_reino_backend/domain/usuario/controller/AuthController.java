package com.nathannolacio.escala_do_reino_backend.domain.usuario.controller;

import com.nathannolacio.escala_do_reino_backend.core.exception.dto.ErrorResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.LoginRequest;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.RegisterRequest;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.UserProfileResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar um novo usuário", description = "Cria um novo usuário no sistema e retorna um token JWT.")
    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirements({})
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @Operation(summary = "Autenticar usuário", description = "Realiza o login e retorna um token JWT.")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirements({})
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @Operation(summary = "Obter perfil do usuário atual", description = "Retorna os detalhes do usuário autenticado através do token.")
    @ApiResponse(responseCode = "200", description = "Perfil obtido com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        return ResponseEntity.ok(service.getCurrentUser());
    }
}

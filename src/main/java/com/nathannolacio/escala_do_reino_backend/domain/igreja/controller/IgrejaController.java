package com.nathannolacio.escala_do_reino_backend.domain.igreja.controller;

import com.nathannolacio.escala_do_reino_backend.core.exception.dto.ErrorResponse;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaRequest;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaResponse;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.service.IgrejaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/igrejas")
@Tag(name = "Igrejas", description = "Gerenciamento de igrejas e sedes")
public class IgrejaController {

    private final IgrejaService service;

    public IgrejaController(IgrejaService service) {
        this.service = service;
    }

    @Operation(summary = "Criar uma nova igreja", description = "Cria uma nova igreja/sede no sistema.")
    @ApiResponse(responseCode = "201", description = "Igreja criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<IgrejaResponse> criar(@Valid @RequestBody IgrejaRequest request) {
        IgrejaResponse response = service.criar(request);
        URI location = URI.create("/igrejas/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Listar todas as igrejas", description = "Retorna uma lista de todas as igrejas cadastradas, permitindo filtro por nome.")
    @ApiResponse(responseCode = "200", description = "Lista obtida com sucesso")
    @GetMapping
    public ResponseEntity<List<IgrejaResponse>> listarTodas(
            @RequestParam(required = false) String nome) {
        return ResponseEntity.ok(service.listarTodas(nome));
    }
}

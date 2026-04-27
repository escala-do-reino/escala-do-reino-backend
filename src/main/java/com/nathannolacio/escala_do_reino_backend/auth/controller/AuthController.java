package com.nathannolacio.escala_do_reino_backend.auth.controller;

import com.nathannolacio.escala_do_reino_backend.auth.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.auth.dto.LoginRequest;
import com.nathannolacio.escala_do_reino_backend.auth.dto.RegisterRequest;
import com.nathannolacio.escala_do_reino_backend.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }
}

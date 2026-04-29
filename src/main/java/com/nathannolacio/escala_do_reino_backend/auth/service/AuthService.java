package com.nathannolacio.escala_do_reino_backend.auth.service;

import com.nathannolacio.escala_do_reino_backend.auth.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.auth.dto.LoginRequest;
import com.nathannolacio.escala_do_reino_backend.auth.dto.RegisterRequest;
import com.nathannolacio.escala_do_reino_backend.auth.entity.Role;
import com.nathannolacio.escala_do_reino_backend.auth.entity.User;
import com.nathannolacio.escala_do_reino_backend.auth.repository.UserRepository;
import com.nathannolacio.escala_do_reino_backend.auth.dto.UserProfileResponse;
import com.nathannolacio.escala_do_reino_backend.auth.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository repository, PasswordEncoder encoder, JwtService jwtService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setRole(Role.USER);

        repository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public UserProfileResponse getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Usuário não autenticado");
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return UserProfileResponse.from(userDetails);
    }

}

package com.nathannolacio.escala_do_reino_backend.domain.usuario.service;

import com.nathannolacio.escala_do_reino_backend.core.security.CustomUserDetails;
import com.nathannolacio.escala_do_reino_backend.core.security.JwtService;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaResponse;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.service.IgrejaService;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.LoginRequest;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.RegisterRequest;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.UserProfileResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.exception.CredenciaisInvalidasException;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.exception.EmailJaCadastradoException;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.Role;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.User;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final IgrejaService igrejaService;

    public AuthService(UserRepository repository, PasswordEncoder encoder, JwtService jwtService, IgrejaService igrejaService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.igrejaService = igrejaService;
    }

    public AuthResponse register(RegisterRequest request) {
        logger.info("Iniciando registro de novo usuário com e-mail: {}", request.email());

        if (repository.findByEmail(request.email()).isPresent()) {
            logger.warn("Tentativa de registro falhou: e-mail {} já está em uso", request.email());
            throw new EmailJaCadastradoException(request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setRole(Role.USER);

        User savedUser = repository.save(user);
        logger.info("Usuário registrado com sucesso. ID: {}, Igreja ID: {}", savedUser.getId(), savedUser.getIgrejaId());

        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getIgrejaId());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Tentativa de login para o e-mail: {}", request.email());

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> {
                    logger.warn("Falha no login: e-mail {} não encontrado", request.email());
                    return new CredenciaisInvalidasException();
                });

        if (!encoder.matches(request.password(), user.getPassword())) {
            logger.warn("Falha no login para o e-mail {}: senha incorreta", request.email());
            throw new CredenciaisInvalidasException();
        }

        logger.info("Login realizado com sucesso para o usuário: {}. Igreja ID: {}", user.getEmail(), user.getIgrejaId());

        String token = jwtService.generateToken(user.getEmail(), user.getIgrejaId());
        return new AuthResponse(token);
    }

    public UserProfileResponse getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Tentativa de obter perfil sem autenticação");
            throw new RuntimeException("Usuário não autenticado");
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        IgrejaResponse igrejaResponse = null;
        if (userDetails.getIgrejaId() != null) {
            try {
                igrejaResponse = igrejaService.buscarResponsePorId(userDetails.getIgrejaId());
            } catch (Exception e) {
                logger.warn("Igreja ID {} não encontrada para o usuário {}", userDetails.getIgrejaId(), userDetails.getUsername());
            }
        }
        
        return UserProfileResponse.from(userDetails, igrejaResponse);
    }

}

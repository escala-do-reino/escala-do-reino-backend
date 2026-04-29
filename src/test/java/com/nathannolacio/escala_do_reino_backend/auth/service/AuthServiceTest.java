package com.nathannolacio.escala_do_reino_backend.auth.service;

import com.nathannolacio.escala_do_reino_backend.auth.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.auth.dto.LoginRequest;
import com.nathannolacio.escala_do_reino_backend.auth.dto.RegisterRequest;
import com.nathannolacio.escala_do_reino_backend.auth.dto.UserProfileResponse;
import com.nathannolacio.escala_do_reino_backend.auth.entity.Role;
import com.nathannolacio.escala_do_reino_backend.auth.entity.User;
import com.nathannolacio.escala_do_reino_backend.auth.repository.UserRepository;
import com.nathannolacio.escala_do_reino_backend.auth.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User defaultUser;

    @BeforeEach
    void setUp() {
        defaultUser = new User(1L, "Nathan", "test@test.com", "encodedPassword", Role.USER);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("Nathan", "test@test.com", "password");
        when(encoder.encode(request.password())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(defaultUser);
        when(jwtService.generateToken(request.email())).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mocked-jwt-token");

        verify(encoder).encode("password");
        verify(repository).save(any(User.class));
        verify(jwtService).generateToken("test@test.com");
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        when(repository.findByEmail(request.email())).thenReturn(Optional.of(defaultUser));
        when(encoder.matches(request.password(), defaultUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(defaultUser.getEmail())).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mocked-jwt-token");
    }

    @Test
    void login_InvalidEmail_ThrowsResponseStatusException() {
        LoginRequest request = new LoginRequest("notfound@test.com", "password");
        when(repository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais inválidas")
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_InvalidPassword_ThrowsResponseStatusException() {
        LoginRequest request = new LoginRequest("test@test.com", "wrongPassword");
        when(repository.findByEmail(request.email())).thenReturn(Optional.of(defaultUser));
        when(encoder.matches(request.password(), defaultUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais inválidas")
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getCurrentUser_Success() {
        CustomUserDetails userDetails = new CustomUserDetails(
                1L, "Nathan", "test@test.com", "encodedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserProfileResponse response = authService.getCurrentUser();

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.name()).isEqualTo("Nathan");
    }

    @Test
    void getCurrentUser_Unauthenticated_ThrowsException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> authService.getCurrentUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuário não autenticado");
    }
}

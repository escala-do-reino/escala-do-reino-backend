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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private IgrejaService igrejaService;

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
        when(repository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(encoder.encode(request.password())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(defaultUser);
        when(jwtService.generateToken(eq(request.email()), any())).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mocked-jwt-token");

        verify(repository).findByEmail("test@test.com");
        verify(encoder).encode("password");
        verify(repository).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsEmailJaCadastradoException() {
        RegisterRequest request = new RegisterRequest("Nathan", "test@test.com", "password");
        when(repository.findByEmail(request.email())).thenReturn(Optional.of(defaultUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("já está cadastrado");

        verify(repository, never()).save(any());
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        when(repository.findByEmail(request.email())).thenReturn(Optional.of(defaultUser));
        when(encoder.matches(request.password(), defaultUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(eq(defaultUser.getEmail()), any())).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mocked-jwt-token");
    }

    @Test
    void login_InvalidEmail_ThrowsCredenciaisInvalidasException() {
        LoginRequest request = new LoginRequest("notfound@test.com", "password");
        when(repository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("E-mail ou senha inválidos");
    }

    @Test
    void login_InvalidPassword_ThrowsCredenciaisInvalidasException() {
        LoginRequest request = new LoginRequest("test@test.com", "wrongPassword");
        when(repository.findByEmail(request.email())).thenReturn(Optional.of(defaultUser));
        when(encoder.matches(request.password(), defaultUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("E-mail ou senha inválidos");
    }

    @Test
    void getCurrentUser_Success() {
        CustomUserDetails userDetails = new CustomUserDetails(
                1L, "Nathan", "test@test.com", "encodedPassword", 10L,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        IgrejaResponse igrejaMock = new IgrejaResponse(10L, "Igreja Teste", "Setor 1", "Rua A", "Cidade B", "SP");
        when(igrejaService.buscarResponsePorId(10L)).thenReturn(igrejaMock);

        UserProfileResponse response = authService.getCurrentUser();

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.igreja()).isNotNull();
        assertThat(response.igreja().nome()).isEqualTo("Igreja Teste");
    }

    @Test
    void getCurrentUser_Unauthenticated_ThrowsException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> authService.getCurrentUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuário não autenticado");
    }
}

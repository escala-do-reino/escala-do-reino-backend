package com.nathannolacio.escala_do_reino_backend.domain.usuario.service;

import com.nathannolacio.escala_do_reino_backend.core.security.JwtService;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.exception.IgrejaNotFoundException;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.repository.IgrejaRepository;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.exception.UsuarioNotFoundException;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IgrejaRepository igrejaRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User defaultUser;

    @BeforeEach
    void setUp() {
        defaultUser = new User(1L, "Nathan", "test@test.com", "encodedPassword", Role.USER);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void vincularIgreja_Success() {
        Long igrejaId = 10L;
        when(userRepository.findByEmailIgnoringTenant("test@test.com")).thenReturn(Optional.of(defaultUser));
        when(igrejaRepository.existsById(igrejaId)).thenReturn(true);
        when(jwtService.generateToken(anyLong(), anyString(), anyLong())).thenReturn("new-token");

        AuthResponse response = userService.vincularIgreja(igrejaId);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("new-token");

        verify(userRepository).updateIgrejaId(defaultUser.getId(), igrejaId);
    }

    @Test
    void vincularIgreja_UserNotFound_ThrowsUsuarioNotFoundException() {
        when(userRepository.findByEmailIgnoringTenant("test@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.vincularIgreja(10L))
                .isInstanceOf(UsuarioNotFoundException.class);
    }

    @Test
    void vincularIgreja_IgrejaNotFound_ThrowsIgrejaNotFoundException() {
        Long igrejaId = 99L;
        when(userRepository.findByEmailIgnoringTenant("test@test.com")).thenReturn(Optional.of(defaultUser));
        when(igrejaRepository.existsById(igrejaId)).thenReturn(false);

        assertThatThrownBy(() -> userService.vincularIgreja(igrejaId))
                .isInstanceOf(IgrejaNotFoundException.class);
    }
}

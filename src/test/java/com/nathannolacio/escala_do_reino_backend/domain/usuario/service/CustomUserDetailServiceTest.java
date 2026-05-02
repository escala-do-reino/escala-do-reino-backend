package com.nathannolacio.escala_do_reino_backend.domain.usuario.service;

import com.nathannolacio.escala_do_reino_backend.core.security.CustomUserDetails;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.Role;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.User;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private CustomUserDetailService service;

    @Test
    void loadUserByUsername_Success() {
        User user = new User(1L, "Nathan", "test@test.com", "password", Role.USER);
        when(repository.findByEmailIgnoringTenant("test@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("test@test.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@test.com");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(repository.findByEmailIgnoringTenant("notfound@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("notfound@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserById_Success() {
        User user = new User(1L, "Nathan", "test@test.com", "password", Role.USER);
        when(repository.findByIdIgnoringTenant(1L)).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserById(1L);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@test.com");
    }

    @Test
    void loadUserById_UserNotFound_ThrowsException() {
        when(repository.findByIdIgnoringTenant(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserById(99L))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}

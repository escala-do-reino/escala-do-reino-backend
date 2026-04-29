package com.nathannolacio.escala_do_reino_backend.auth.service;

import com.nathannolacio.escala_do_reino_backend.auth.entity.Role;
import com.nathannolacio.escala_do_reino_backend.auth.entity.User;
import com.nathannolacio.escala_do_reino_backend.auth.repository.UserRepository;
import com.nathannolacio.escala_do_reino_backend.auth.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.NoSuchElementException;
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
        when(repository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("test@test.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        assertThat(userDetails.getUsername()).isEqualTo("test@test.com");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(repository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("notfound@test.com"))
                .isInstanceOf(NoSuchElementException.class);
    }
}

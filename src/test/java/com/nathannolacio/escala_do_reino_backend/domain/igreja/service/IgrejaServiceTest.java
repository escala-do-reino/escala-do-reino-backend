package com.nathannolacio.escala_do_reino_backend.domain.igreja.service;

import com.nathannolacio.escala_do_reino_backend.core.security.JwtService;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaCreateResponse;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaRequest;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaResponse;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.model.Igreja;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.repository.IgrejaRepository;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.exception.UsuarioJaVinculadoException;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IgrejaServiceTest {

    @Mock
    private IgrejaRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private IgrejaService igrejaService;

    private User defaultUser;

    @BeforeEach
    void setUp() {
        defaultUser = new User(1L, "Nathan", "test@test.com", "password", Role.USER);
        defaultUser.setIgrejaId(0L); // Igreja do sistema
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("test@test.com", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void criar_Success() {
        IgrejaRequest request = new IgrejaRequest(
                "Igreja Teste", "Rua A", "100", "Sala 1", "Bairro X", "Cidade Y", "SP", "00000-000"
        );

        Igreja igrejaSalva = new Igreja();
        igrejaSalva.setId(10L);
        igrejaSalva.setNome("Igreja Teste");

        when(userRepository.findByEmailIgnoringTenant("test@test.com")).thenReturn(Optional.of(defaultUser));
        when(repository.save(any(Igreja.class))).thenReturn(igrejaSalva);
        when(userRepository.updateIgrejaId(anyLong(), anyLong())).thenReturn(1);
        when(jwtService.generateToken(anyLong(), anyString(), anyLong())).thenReturn("new-jwt-token");

        IgrejaCreateResponse response = igrejaService.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.igreja().id()).isEqualTo(10L);
        assertThat(response.token()).isEqualTo("new-jwt-token");

        verify(repository).save(any(Igreja.class));
        verify(userRepository).updateIgrejaId(defaultUser.getId(), 10L);
        verify(jwtService).generateToken(anyLong(), anyString(), anyLong());
    }

    @Test
    void criar_UserAlreadyLinked_ThrowsUsuarioJaVinculadoException() {
        defaultUser.setIgrejaId(1L); // Já vinculado
        when(userRepository.findByEmailIgnoringTenant("test@test.com")).thenReturn(Optional.of(defaultUser));

        IgrejaRequest request = new IgrejaRequest(
                "Nova Igreja", "Rua A", "100", null, "Bairro X", "Cidade Y", "SP", "00000-000"
        );

        assertThatThrownBy(() -> igrejaService.criar(request))
                .isInstanceOf(UsuarioJaVinculadoException.class);
    }

    @Test
    void listarTodas_WithFiltro() {
        String filtro = "Assembleia";
        Igreja igreja = new Igreja();
        igreja.setNome("Assembleia de Deus");
        
        when(repository.findByNomeContainingIgnoreCase(filtro)).thenReturn(List.of(igreja));

        List<IgrejaResponse> result = igrejaService.listarTodas(filtro);

        assertThat(result).hasSize(1);
        verify(repository).findByNomeContainingIgnoreCase(filtro);
    }

    @Test
    void listarTodas_WithoutFiltro() {
        when(repository.findAll()).thenReturn(List.of(new Igreja(), new Igreja()));

        List<IgrejaResponse> result = igrejaService.listarTodas(null);

        assertThat(result).hasSize(2);
        verify(repository).findAll();
    }
}

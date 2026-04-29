package com.nathannolacio.escala_do_reino_backend.domain.usuario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.LoginRequest;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.RegisterRequest;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.UserProfileResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nathannolacio.escala_do_reino_backend.core.security.JwtAuthenticationFilter;
import com.nathannolacio.escala_do_reino_backend.core.security.JwtService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables Spring Security filters for simple controller testing
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void register_ReturnsAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest("Nathan", "test@test.com", "password");
        AuthResponse response = new AuthResponse("mocked-token");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-token"));
    }

    @Test
    void login_ReturnsAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        AuthResponse response = new AuthResponse("mocked-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-token"));
    }

    @Test
    void getCurrentUser_ReturnsUserProfileResponse() throws Exception {
        UserProfileResponse response = new UserProfileResponse(1L, "Nathan", "test@test.com", List.of("ROLE_USER"));

        when(authService.getCurrentUser()).thenReturn(response);

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Nathan"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }
}

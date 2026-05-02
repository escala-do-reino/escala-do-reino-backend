package com.nathannolacio.escala_do_reino_backend.core.security;

import com.nathannolacio.escala_do_reino_backend.domain.usuario.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                final String token = authHeader.substring(7);
                final Long userId = jwtService.extractUserId(token);
                final Long igrejaId = jwtService.extractIgrejaId(token);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(userId);
                    
                    try {
                        // Validação Crítica: O Tenant do Token deve bater com o Tenant do Usuário no Banco
                        if (igrejaId != null && !igrejaId.equals(userDetails.getIgrejaId())) {
                            logger.error("Divergência de Tenant detectada! Token: " + igrejaId + ", Banco: " + userDetails.getIgrejaId());
                            throw new org.springframework.security.core.AuthenticationException("Token inválido para esta igreja") {};
                        }

                        if (igrejaId != null) {
                            TenantContext.setCurrentTenant(igrejaId);
                        }

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } catch (Exception e) {
                        logger.error("Erro ao configurar contexto de segurança para o usuário ID: " + userId, e);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}

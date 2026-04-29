package com.nathannolacio.escala_do_reino_backend.domain.usuario.dto;

import com.nathannolacio.escala_do_reino_backend.core.security.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        List<String> roles
) {
    public static UserProfileResponse from(CustomUserDetails user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }
}

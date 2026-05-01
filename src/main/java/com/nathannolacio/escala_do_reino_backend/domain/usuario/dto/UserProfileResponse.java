package com.nathannolacio.escala_do_reino_backend.domain.usuario.dto;

import com.nathannolacio.escala_do_reino_backend.core.security.CustomUserDetails;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaResponse;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        List<String> roles,
        IgrejaResponse igreja
) {
    public static UserProfileResponse from(CustomUserDetails user, IgrejaResponse igreja) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList(),
                igreja
        );
    }
}

package com.nathannolacio.escala_do_reino_backend.domain.usuario.service;

import com.nathannolacio.escala_do_reino_backend.core.security.CustomUserDetails;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.User;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = repository.findByEmailIgnoringTenant(email)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuário não encontrado: " + email));
        return toUserDetails(user);
    }

    public UserDetails loadUserById(Long id) {
        User user = repository.findByIdIgnoringTenant(id)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuário não encontrado com ID: " + id));
        return toUserDetails(user);
    }

    private UserDetails toUserDetails(User user) {
        return new CustomUserDetails(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getIgrejaId(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}

package com.nathannolacio.escala_do_reino_backend.core.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String name;
    private String email;
    private String password;
    private Long igrejaId;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(
            Long id,
            String name,
            String email,
            String password,
            Long igrejaId,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.igrejaId = igrejaId;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getIgrejaId() {
        return igrejaId;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

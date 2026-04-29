package com.nathannolacio.escala_do_reino_backend.domain.usuario.repository;

import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

package com.nathannolacio.escala_do_reino_backend.domain.usuario.repository;

import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailIgnoringTenant(String email);

    @Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    Optional<User> findByIdIgnoringTenant(Long id);

    @Modifying
    @Query(value = "UPDATE users SET igreja_id = :igrejaId WHERE id = :userId", nativeQuery = true)
    void updateIgrejaId(Long userId, Long igrejaId);
}

package com.nathannolacio.escala_do_reino_backend.domain.igreja.repository;

import com.nathannolacio.escala_do_reino_backend.domain.igreja.model.Igreja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IgrejaRepository extends JpaRepository<Igreja, Long> {
    List<Igreja> findByNomeContainingIgnoreCase(String nome);
}

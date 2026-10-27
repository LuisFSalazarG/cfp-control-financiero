package com.cfp.controlfinanciero.repository;

import com.cfp.controlfinanciero.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByCodigo(String codigo);
}

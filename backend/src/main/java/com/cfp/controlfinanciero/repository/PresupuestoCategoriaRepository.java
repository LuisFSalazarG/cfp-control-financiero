package com.cfp.controlfinanciero.repository;

import com.cfp.controlfinanciero.domain.Categoria;
import com.cfp.controlfinanciero.domain.PresupuestoCategoria;
import com.cfp.controlfinanciero.domain.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

public interface PresupuestoCategoriaRepository extends JpaRepository<PresupuestoCategoria, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PresupuestoCategoria> findByUsuarioAndCategoriaAndAnioAndMes(
            Usuario usuario, Categoria categoria, int anio, int mes);
}

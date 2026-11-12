package com.cfp.controlfinanciero.repository;

import com.cfp.controlfinanciero.domain.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    Optional<Presupuesto> findByUsuarioIdAndMesAndAnio(Long usuarioId, Integer mes, Integer anio);
}
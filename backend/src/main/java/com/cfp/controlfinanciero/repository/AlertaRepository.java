package com.cfp.controlfinanciero.repository;

import com.cfp.controlfinanciero.domain.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndNivelAndMesAndAnio(Long usuarioId, Integer nivel, Integer mes, Integer anio);
}
package com.cfp.controlfinanciero.repository;

import com.cfp.controlfinanciero.domain.Gasto;
import com.cfp.controlfinanciero.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByUsuarioAndFechaBetween(Usuario usuario, LocalDate inicio, LocalDate fin);

    @Query("SELECT COALESCE(SUM(g.monto),0) FROM Gasto g WHERE g.usuario = :usuario AND g.fecha BETWEEN :inicio AND :fin")
    Long sumMontoByUsuarioAndFechaBetween(@Param("usuario") Usuario usuario, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(g.monto),0) FROM Gasto g WHERE g.usuario = :usuario AND g.categoria = :categoria AND g.fecha BETWEEN :inicio AND :fin")
    Long sumMontoByUsuarioAndCategoriaAndFechaBetween(@Param("usuario") Usuario usuario, @Param("categoria") com.cfp.controlfinanciero.domain.Categoria categoria, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    Optional<Gasto> findByIdAndUsuario(Long id, Usuario usuario);
}

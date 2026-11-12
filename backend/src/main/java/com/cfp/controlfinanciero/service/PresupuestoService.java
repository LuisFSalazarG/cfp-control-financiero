package com.cfp.controlfinanciero.service;

import com.cfp.controlfinanciero.domain.Alerta;
import com.cfp.controlfinanciero.domain.Presupuesto;
import com.cfp.controlfinanciero.domain.Usuario;
import com.cfp.controlfinanciero.repository.AlertaRepository;
import com.cfp.controlfinanciero.repository.GastoRepository;
import com.cfp.controlfinanciero.repository.PresupuestoRepository;
import com.cfp.controlfinanciero.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final GastoRepository gastoRepository;
    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository, GastoRepository gastoRepository, AlertaRepository alertaRepository, UsuarioRepository usuarioRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.gastoRepository = gastoRepository;
        this.alertaRepository = alertaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Presupuesto definirPresupuesto(Long usuarioId, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del presupuesto debe ser mayor a cero.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        LocalDate hoy = LocalDate.now();
        int mes = hoy.getMonthValue();
        int anio = hoy.getYear();

        Presupuesto presupuesto = presupuestoRepository.findByUsuarioIdAndMesAndAnio(usuarioId, mes, anio)
                .orElse(new Presupuesto());

        presupuesto.setUsuario(usuario);
        presupuesto.setMontoLimite(monto);
        presupuesto.setMes(mes);
        presupuesto.setAnio(anio);

        return presupuestoRepository.save(presupuesto);
    }

    @Transactional
    public void verificarYGenerarAlertas(Long usuarioId, int mes, int anio) {
        Presupuesto presupuesto = presupuestoRepository.findByUsuarioIdAndMesAndAnio(usuarioId, mes, anio)
                .orElse(null);

        if (presupuesto == null || presupuesto.getMontoLimite().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal totalGastado = gastoRepository.sumarGastosPorUsuarioYMes(usuarioId, mes, anio);
        if (totalGastado == null) {
            totalGastado = BigDecimal.ZERO;
        }

        BigDecimal porcentaje = totalGastado.divide(presupuesto.getMontoLimite(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        evaluarYCrearAlerta(usuario, 70, porcentaje, presupuesto.getMontoLimite(), totalGastado, mes, anio);
        evaluarYCrearAlerta(usuario, 80, porcentaje, presupuesto.getMontoLimite(), totalGastado, mes, anio);
        evaluarYCrearAlerta(usuario, 90, porcentaje, presupuesto.getMontoLimite(), totalGastado, mes, anio);
    }

    private void evaluarYCrearAlerta(Usuario usuario, int nivel, BigDecimal porcentajeActual, BigDecimal presupuestoTotal, BigDecimal totalGastos, int mes, int anio) {
        if (porcentajeActual.compareTo(new BigDecimal(nivel)) >= 0) {
            boolean existe = alertaRepository.existsByUsuarioIdAndNivelAndMesAndAnio(usuario.getId(), nivel, mes, anio);
            if (!existe) {
                BigDecimal restante = presupuestoTotal.subtract(totalGastos);
                String mensaje = "Has alcanzado el " + nivel + "% de tu presupuesto mensual.";
                String sugerencia = "Llegaste al " + nivel + " % de tu gasto mensual. Te quedan $" + restante + "; revisa tus gastos.";

                Alerta alerta = new Alerta(usuario, nivel, mes, anio, mensaje, sugerencia);
                alertaRepository.save(alerta);
            }
        }
    }

    public double obtenerPorcentajeActual(Long usuarioId, int mes, int anio) {
        Presupuesto presupuesto = presupuestoRepository.findByUsuarioIdAndMesAndAnio(usuarioId, mes, anio)
                .orElse(null);
        if (presupuesto == null || presupuesto.getMontoLimite().compareTo(BigDecimal.ZERO) <= 0) return 0.0;

        BigDecimal totalGastado = gastoRepository.sumarGastosPorUsuarioYMes(usuarioId, mes, anio);
        if (totalGastado == null) return 0.0;

        return totalGastado.divide(presupuesto.getMontoLimite(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).doubleValue();
    }

    public List<Alerta> obtenerAlertas(Long usuarioId) {
        return alertaRepository.findByUsuarioId(usuarioId);
    }
}
package com.cfp.controlfinanciero.service;

import com.cfp.controlfinanciero.domain.Alerta;
import com.cfp.controlfinanciero.domain.Presupuesto;
import com.cfp.controlfinanciero.domain.Usuario;
import com.cfp.controlfinanciero.repository.AlertaRepository;
import com.cfp.controlfinanciero.repository.GastoRepository;
import com.cfp.controlfinanciero.repository.PresupuestoRepository;
import com.cfp.controlfinanciero.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

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

    public void verificarYGenerarAlertas(Long usuarioId, int mes, int anio) {
        Presupuesto presupuesto = presupuestoRepository.findByUsuarioIdAndMesAndAnio(usuarioId, mes, anio)
                .orElseThrow(() -> new RuntimeException("No hay presupuesto definido para este mes"));

        BigDecimal totalGastado = gastoRepository.sumarGastosPorUsuarioYMes(usuarioId, mes, anio);
        if (totalGastado == null) {
            totalGastado = BigDecimal.ZERO;
        }

        // (totalGastado / montoPresupuesto) * 100
        BigDecimal porcentaje = totalGastado.divide(presupuesto.getMontoLimite(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (porcentaje.compareTo(new BigDecimal("90")) >= 0) {
            crearAlerta(usuario, "Superado el 90% del presupuesto");
        } else if (porcentaje.compareTo(new BigDecimal("80")) >= 0) {
            crearAlerta(usuario, "Superado el 80% del presupuesto");
        } else if (porcentaje.compareTo(new BigDecimal("70")) >= 0) {
            crearAlerta(usuario, "Superado el 70% del presupuesto");
        }
    }

    private void crearAlerta(Usuario usuario, String tipoMensaje) {
        Alerta alerta = new Alerta();
        alerta.setTipoAlerta(tipoMensaje);
        alerta.setFecha(LocalDate.now());
        alerta.setUsuario(usuario);
        alertaRepository.save(alerta);
    }

    // Método para exponer los datos al controlador
    public double obtenerPorcentajeActual(Long usuarioId, int mes, int anio) {
        Presupuesto presupuesto = presupuestoRepository.findByUsuarioIdAndMesAndAnio(usuarioId, mes, anio)
                .orElse(null);
        if (presupuesto == null) return 0.0;

        BigDecimal totalGastado = gastoRepository.sumarGastosPorUsuarioYMes(usuarioId, mes, anio);
        if (totalGastado == null) return 0.0;

        return totalGastado.divide(presupuesto.getMontoLimite(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).doubleValue();
    }

    public List<Alerta> obtenerAlertas(Long usuarioId) {
        return alertaRepository.findByUsuarioId(usuarioId);
    }
}
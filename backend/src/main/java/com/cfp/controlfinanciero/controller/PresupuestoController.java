package com.cfp.controlfinanciero.controller;

import com.cfp.controlfinanciero.domain.Alerta;
import com.cfp.controlfinanciero.service.PresupuestoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @GetMapping("/alertas/{usuarioId}")
    public ResponseEntity obtenerEstadoPresupuesto(@PathVariable Long usuarioId) {
        LocalDate hoy = LocalDate.now();

        // Dispara la verificación antes de devolver los datos (opcional, dependiendo de tu flujo)
        presupuestoService.verificarYGenerarAlertas(usuarioId, hoy.getMonthValue(), hoy.getYear());

        double porcentaje = presupuestoService.obtenerPorcentajeActual(usuarioId, hoy.getMonthValue(), hoy.getYear());
        List<Alerta> alertas = presupuestoService.obtenerAlertas(usuarioId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("porcentajeGastado", porcentaje);
        respuesta.put("alertasActivas", alertas);

        return ResponseEntity.ok(respuesta);
    }
}
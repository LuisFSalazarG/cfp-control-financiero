package com.cfp.controlfinanciero.controller;

import com.cfp.controlfinanciero.domain.Alerta;
import com.cfp.controlfinanciero.domain.Presupuesto;
import com.cfp.controlfinanciero.domain.Usuario;
import com.cfp.controlfinanciero.service.PresupuestoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping
    public ResponseEntity<?> definirPresupuesto(@AuthenticationPrincipal Usuario usuarioLogueado,
                                                @RequestBody Map<String, BigDecimal> body) {
        BigDecimal monto = body.get("monto");
        Presupuesto nuevoPresupuesto = presupuestoService.definirPresupuesto(usuarioLogueado.getId(), monto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPresupuesto);
    }

    @GetMapping("/alertas")
    public ResponseEntity<?> obtenerEstadoPresupuesto(@AuthenticationPrincipal Usuario usuarioLogueado) {
        LocalDate hoy = LocalDate.now();

        presupuestoService.verificarYGenerarAlertas(usuarioLogueado.getId(), hoy.getMonthValue(), hoy.getYear());

        double porcentaje = presupuestoService.obtenerPorcentajeActual(usuarioLogueado.getId(), hoy.getMonthValue(), hoy.getYear());
        List<Alerta> alertas = presupuestoService.obtenerAlertas(usuarioLogueado.getId());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("porcentajeGastado", porcentaje);
        respuesta.put("alertasActivas", alertas);

        return ResponseEntity.ok(respuesta);
    }
}
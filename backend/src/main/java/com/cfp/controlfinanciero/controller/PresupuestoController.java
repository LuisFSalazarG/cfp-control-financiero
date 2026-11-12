package com.cfp.controlfinanciero.controller;

import com.cfp.controlfinanciero.domain.Alerta;
import com.cfp.controlfinanciero.domain.Presupuesto;
import com.cfp.controlfinanciero.domain.Usuario;
import com.cfp.controlfinanciero.dto.PresupuestoDTO;
import com.cfp.controlfinanciero.repository.UsuarioRepository;
import com.cfp.controlfinanciero.service.PresupuestoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UsuarioRepository usuarioRepository;

    public PresupuestoController(PresupuestoService presupuestoService, UsuarioRepository usuarioRepository) {
        this.presupuestoService = presupuestoService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<PresupuestoDTO> definirPresupuesto(@AuthenticationPrincipal UserDetails userDetails,
                                                             @RequestBody Map<String, BigDecimal> body) {
        // 1. Obtener el usuario autenticado desde la base de datos
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear el presupuesto
        BigDecimal monto = body.get("monto");
        Presupuesto nuevoPresupuesto = presupuestoService.definirPresupuesto(usuario.getId(), monto);

        // 3. Mapear al DTO para no exponer el hash de la contraseña
        PresupuestoDTO responseDTO = new PresupuestoDTO(
                nuevoPresupuesto.getMes(),
                nuevoPresupuesto.getAnio(),
                nuevoPresupuesto.getMontoLimite()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/alertas")
    public ResponseEntity<?> obtenerEstadoPresupuesto(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDate hoy = LocalDate.now();

        presupuestoService.verificarYGenerarAlertas(usuario.getId(), hoy.getMonthValue(), hoy.getYear());

        double porcentaje = presupuestoService.obtenerPorcentajeActual(usuario.getId(), hoy.getMonthValue(), hoy.getYear());
        List<Alerta> alertas = presupuestoService.obtenerAlertas(usuario.getId());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("porcentajeGastado", porcentaje);
        respuesta.put("alertasActivas", alertas);

        return ResponseEntity.ok(respuesta);
    }
}
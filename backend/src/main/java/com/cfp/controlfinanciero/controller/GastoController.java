package com.cfp.controlfinanciero.controller;

import com.cfp.controlfinanciero.dto.GastoMesResponse;
import com.cfp.controlfinanciero.dto.GastoRequest;
import com.cfp.controlfinanciero.dto.GastoResponse;
import com.cfp.controlfinanciero.exception.BadRequestException;
import com.cfp.controlfinanciero.service.GastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {
    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @PostMapping
    public ResponseEntity<GastoResponse> registrar(@RequestBody GastoRequest request) {
        GastoResponse res = gastoService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<GastoMesResponse> listar(@RequestParam(name = "mes", required = false) String mes) {
        return ResponseEntity.ok(gastoService.listarPorMes(mes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoResponse> obtener(@PathVariable String id) {
        Long parsed;
        try {
            parsed = Long.parseLong(id.trim());
        } catch (Exception e) {
            throw new BadRequestException("El id del gasto debe ser un número");
        }
        return ResponseEntity.ok(gastoService.obtenerPorId(parsed));
    }
}

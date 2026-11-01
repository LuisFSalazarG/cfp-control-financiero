package com.cfp.controlfinanciero.controller;

import com.cfp.controlfinanciero.domain.Categoria;
import com.cfp.controlfinanciero.dto.CategoriaResponse;
import com.cfp.controlfinanciero.repository.CategoriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        List<CategoriaResponse> list = categoriaRepository.findAll().stream()
                .map(c -> new CategoriaResponse(c.getCodigo(), c.getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> notAllowedPost() {
        return ResponseEntity.status(405).body(java.util.Map.of("message", "Las categorías son fijas y no se pueden modificar"));
    }

    @PutMapping
    public ResponseEntity<?> notAllowedPut() {
        return ResponseEntity.status(405).body(java.util.Map.of("message", "Las categorías son fijas y no se pueden modificar"));
    }

    @DeleteMapping
    public ResponseEntity<?> notAllowedDelete() {
        return ResponseEntity.status(405).body(java.util.Map.of("message", "Las categorías son fijas y no se pueden modificar"));
    }

    @PatchMapping
    public ResponseEntity<?> notAllowedPatch() {
        return ResponseEntity.status(405).body(java.util.Map.of("message", "Las categorías son fijas y no se pueden modificar"));
    }
}

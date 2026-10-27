package com.cfp.controlfinanciero.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GastoResponse {
    private Long id;
    private Long monto;
    private String categoria;
    private LocalDate fecha;
    private String nota;
    private Long totalMes;
    private Long totalCategoria;
    private LocalDateTime fechaRegistro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMonto() { return monto; }
    public void setMonto(Long monto) { this.monto = monto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    public Long getTotalMes() { return totalMes; }
    public void setTotalMes(Long totalMes) { this.totalMes = totalMes; }
    public Long getTotalCategoria() { return totalCategoria; }
    public void setTotalCategoria(Long totalCategoria) { this.totalCategoria = totalCategoria; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}

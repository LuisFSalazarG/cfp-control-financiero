package com.cfp.controlfinanciero.dto;

import java.time.LocalDateTime;

public class AlertaDTO {
    private Integer nivel;
    private Integer mes;
    private Integer anio;
    private String mensaje;
    private String sugerencia;
    private LocalDateTime fechaCreacion;

    public AlertaDTO(Integer nivel, Integer mes, Integer anio, String mensaje, String sugerencia, LocalDateTime fechaCreacion) {
        this.nivel = nivel;
        this.mes = mes;
        this.anio = anio;
        this.mensaje = mensaje;
        this.sugerencia = sugerencia;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getNivel() { return nivel; }
    public Integer getMes() { return mes; }
    public Integer getAnio() { return anio; }
    public String getMensaje() { return mensaje; }
    public String getSugerencia() { return sugerencia; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
package com.cfp.controlfinanciero.dto;

import java.math.BigDecimal;

public class PresupuestoDTO {
    private Integer mes;
    private Integer anio;
    private BigDecimal monto;

    public PresupuestoDTO() {
    }

    public PresupuestoDTO(Integer mes, Integer anio, BigDecimal monto) {
        this.mes = mes;
        this.anio = anio;
        this.monto = monto;
    }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}
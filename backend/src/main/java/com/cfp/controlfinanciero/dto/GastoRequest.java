package com.cfp.controlfinanciero.dto;

public class GastoRequest {
    private Object monto;
    private String categoria;
    private String fecha;
    private String nota;

    public Object getMonto() { return monto; }
    public void setMonto(Object monto) { this.monto = monto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
}

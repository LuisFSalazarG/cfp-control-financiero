package com.cfp.controlfinanciero.dto;

import java.util.List;

public class GastoMesResponse {
    private List<GastoResponse> gastos;
    private Long totalMes;

    public List<GastoResponse> getGastos() { return gastos; }
    public void setGastos(List<GastoResponse> gastos) { this.gastos = gastos; }
    public Long getTotalMes() { return totalMes; }
    public void setTotalMes(Long totalMes) { this.totalMes = totalMes; }
}

package com.cfp.controlfinanciero.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private BigDecimal montoLimite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public BigDecimal getMontoLimite() {
        return montoLimite;
    }

    public void setMontoLimite(BigDecimal montoLimite) {
        this.montoLimite = montoLimite;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
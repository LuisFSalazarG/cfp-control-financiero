package com.cfp.controlfinanciero.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "presupuesto_categoria", uniqueConstraints = @UniqueConstraint(
        columnNames = {"usuario_id", "categoria_id", "anio", "mes"}))
public class PresupuestoCategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    private int anio;
    private int mes;

    @Column(name = "monto_limite", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoLimite;

    @Column(name = "alerta_70_emitida", nullable = false)
    private boolean alertaEmitida;

    public boolean marcarAlertaSiCorresponde(long totalGastado) {
        BigDecimal umbral = montoLimite.multiply(new BigDecimal("0.70"));
        if (alertaEmitida || BigDecimal.valueOf(totalGastado).compareTo(umbral) < 0) {
            return false;
        }
        alertaEmitida = true;
        return true;
    }
}

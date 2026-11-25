# Alerta al registrar un gasto · CFP-18

Aplicar `db/migraciones/V2_1__alerta_por_categoria.sql` una vez en MySQL después de V2 y antes de iniciar esta versión con `ddl-auto=validate`. La columna nueva conserva el estado de emisión para cada presupuesto mensual por categoría. El flujo de configuración de esos presupuestos corresponde a CFP-39.

`POST /api/gastos` conserva su entrada y su respuesta 201. Cuando el gasto hace que el total acumulado alcance o supere el 70 % de un límite configurado, la respuesta añade `alertaCategoria`. La evaluación usa el usuario autenticado, la categoría y el año y mes de la fecha del gasto. La primera evaluación sobre un presupuesto existente que ya se encuentre por encima del umbral también genera el aviso.

```json
{
  "categoria": "ENTRETENIMIENTO",
  "nivel": 70,
  "mensaje": "Alcanzaste el 70 % de tu presupuesto de Entretenimiento.",
  "sugerencia": "Revisa los gastos previstos para esta categoría antes de hacer otra compra."
}
```

Este objeto se omite si falta el presupuesto, el total sigue por debajo del umbral o el aviso ya se emitió. El gasto y la marca se confirman en una transacción. Los registros concurrentes sobre el mismo presupuesto se serializan al bloquear su fila. Al editar el límite mensual, CFP-39 deberá conservar la fila y su marca de emisión.

El caso CA-05 usa un límite de $200.000. Después de acumular $120.000, un gasto de $20.000 activa el aviso exactamente al 70 %. El presupuesto de otro mes tiene su propia marca.

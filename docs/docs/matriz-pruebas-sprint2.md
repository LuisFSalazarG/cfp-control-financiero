# Matriz de Pruebas - Sprint 2 (Calidad)

| Caso | Requisito / CA | Qué se prueba | Datos de Entrada | Resultado Esperado |
|---|---|---|---|---|
| CP-13 | CA-12 | Definir presupuesto mensual válido | Monto: $1.000.000 | Código 200/201 OK |
| CP-14 | CA-12 | Presupuesto en 0 o negativo | Monto: $0 o -$50.000 | Código 400 Bad Request |
| CP-15 | CA-13 | Consultar porcentaje de gasto | Gastado $400.000 de $1.000.000 | Código 200 OK (Retorna 40%) |
| CP-16 | CA-04 / CA-06 | Alerta del 70% | Gastos acumulados $700.000 | Genera alerta del 70% |
| CP-17 | CA-04 / CA-06 | Alerta del 80% y 90% | Gastos acumulados $800.000 y $900.000 | Genera alertas respectivas |
| CP-18 | RF-08 | Consultar endpoint de alertas | GET /api/presupuestos/alertas/{usuarioId} | Código 200 OK con lista de alertas |

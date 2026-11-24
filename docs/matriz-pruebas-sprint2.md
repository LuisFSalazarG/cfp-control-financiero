### Casos de Prueba Adicionales (Sprint 2)

| Caso | Requisito / CA | Qué se prueba | Datos de Entrada | Resultado Esperado |
|---|---|---|---|---|
| CP-13 | RF-07 / CA-12 | Definir gasto mensual válido | POST /api/presupuestos con monto 1000000 | 201 con mes, anio y montoLimite |
| CP-14 | RN-01 | Gasto mensual en 0 | POST /api/presupuestos con monto 0 | 400 y no se guarda |
| CP-15 | RF-08 / CA-13 | Porcentaje gastado | Gasto mensual $1.000.000 y $400.000 gastados | porcentajeGastado = 40 |
| CP-16 | RF-09 / CA-04 | Alerta del 70 % | Gastos acumulados de $700.000 | Alerta de nivel 70 con sugerencia |
| CP-18 | CA-06 | Sin alerta antes del 70 % | Gastos acumulados de $600.000 | alertasActivas vacía |
| **CP-17** | CA-17 / CA-18 | Alerta del 80 % y 90 % | Gastos acumulados de $800.000 y luego $900.000 (gasto mensual $1.000.000) | Una alerta del 80 % y luego una del 90 % |
| **CP-19** | CA-19 | La alerta no se repite | Ya salió la del 70 %; se registran $30.000 más (73 %) | No se crea otra alerta del 70 % |
| **CP-20** | CA-20 | Reinicio mensual | En octubre salió la del 70 %; en noviembre llega otra vez al 70 % | Sale de nuevo la alerta del 70 % |
| **CP-21** | RN-06 | La alerta trae sugerencia | Cualquier alerta generada | El mensaje incluye una sugerencia |
| **CP-22** | Seguridad | Consultar sin iniciar sesión | GET alertas sin usuario ni contraseña | Código 401 |
| **CP-23** | RNF-03 | Un usuario no ve datos de otro | El usuario B consulta las alertas del usuario A | No se muestran (403 o 404) |
| **CP-24** | Regresión | Lo del Sprint 1 sigue funcionando | Casos CP-01 a CP-12 | Mismo resultado que en el Sprint 1 |

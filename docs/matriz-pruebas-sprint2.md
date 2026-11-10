### Casos de Prueba Adicionales (Sprint 2)

| Caso | Requisito / CA | Qué se prueba | Datos de Entrada | Resultado Esperado |
|---|---|---|---|---|
| **CP-17** | CA-17 / CA-18 | Alerta del 80 % y 90 % | Gastos acumulados de $800.000 y luego $900.000 (gasto mensual $1.000.000) | Una alerta del 80 % y luego una del 90 % |
| **CP-19** | CA-19 | La alerta no se repite | Ya salió la del 70 %; se registran $30.000 más (73 %) | No se crea otra alerta del 70 % |
| **CP-20** | CA-20 | Reinicio mensual | En octubre salió la del 70 %; en noviembre llega otra vez al 70 % | Sale de nuevo la alerta del 70 % |
| **CP-21** | RN-06 | La alerta trae sugerencia | Cualquier alerta generada | El mensaje incluye una sugerencia |
| **CP-22** | Seguridad | Consultar sin iniciar sesión | GET alertas sin usuario ni contraseña | Código 401 |
| **CP-23** | RNF-03 | Un usuario no ve datos de otro | El usuario B consulta las alertas del usuario A | No se muestran (403 o 404) |
| **CP-24** | Regresión | Lo del Sprint 1 sigue funcionando | Casos CP-01 a CP-12 | Mismo resultado que en el Sprint 1 |

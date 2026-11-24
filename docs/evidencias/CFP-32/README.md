# Evidencias CFP-32 — Pruebas del Sprint 2

- **Ítem de Jira:** CFP-32 (Calidad)
- **Responsable:** Santiago Triana Carvajal (Calidad)
- **Ejecución técnica:** Luis Eduardo García Vázquez (Producto), en apoyo a Calidad
- **Fecha de ejecución:** 2026-11-21
- **Rama:** `develop` (commit `113c9a9`)
- **Entorno:** Spring Boot 3.3.5, Java 21, H2 en memoria (perfil de pruebas)
- **Ejecución:** script automatizado contra `http://localhost:8080` con usuarios de prueba `a@ejemplo.invalid` y `b@ejemplo.invalid`
- **Registro completo:** [`evidencia-cfp32.txt`](./evidencia-cfp32.txt)
- **Resultado global:** 20 verificaciones · 20 PASÓ · 0 FALLÓ · CP-20 no ejecutable por API (requiere cambio de mes)

## Resumen

| Caso | Requisito / CA | Esperado | Obtenido | Estado |
|---|---|---|---|---|
| CP-13 | RF-07 / CA-12 | 201 con mes, anio y monto | 201 `{"mes":11,"anio":2026,"monto":1000000}` | PASÓ |
| CP-14 | RN-01 | 400 y no guarda | 400 `Solicitud inválida` | PASÓ |
| CP-15 | RF-08 / CA-13 | porcentajeGastado = 40 | `40.0` | PASÓ |
| CP-16 | RF-09 / CA-04 | alerta 70 con sugerencia | alerta nivel 70 + sugerencia | PASÓ |
| CP-18 | CA-06 | alertasActivas vacía | `[]` | PASÓ |
| CP-17 | CA-17 / CA-18 | alerta 80 y luego 90 | alertas 80 y 90 | PASÓ |
| CP-19 | CA-19 | no repite la del 70 | solo la alerta 70 existente | PASÓ |
| CP-20 | CA-20 | reinicio mensual | no ejecutable por API (requiere cambio de mes) | N/A |
| CP-21 | RN-06 | toda alerta trae sugerencia | 3 alertas con sugerencia | PASÓ |
| CP-22 | Seguridad | 401 sin sesión | 401 | PASÓ |
| CP-23 | RNF-03 | B no ve datos de A | `[]` y `0.0` | PASÓ |
| CP-24 | Regresión | CP-01 a CP-12 igual | categorías 200, monto 0 → 400, categoría inválida → 422, listado 200 | PASÓ |

## Detalle por caso

### CP-13 — Definir gasto mensual válido
- **Requisito / CA:** RF-07 / CA-12
- **Datos de entrada:** `POST /api/presupuestos` `{"monto":1000000}`
- **Resultado esperado:** 201 con mes, anio y monto
- **Resultado obtenido:** `201` — `{"mes":11,"anio":2026,"monto":1000000}`
- **Estado:** PASÓ

### CP-14 — Gasto mensual en 0
- **Requisito / CA:** RN-01
- **Datos de entrada:** `POST /api/presupuestos` `{"monto":0}`
- **Resultado esperado:** 400 y no guarda
- **Resultado obtenido:** `400` — `{"message":"Solicitud inválida"}`
- **Estado:** PASÓ

### CP-15 — Porcentaje gastado
- **Requisito / CA:** RF-08 / CA-13
- **Datos de entrada:** presupuesto $1.000.000 y gasto de $400.000
- **Resultado esperado:** `porcentajeGastado = 40`
- **Resultado obtenido:** `200` — `{"alertasActivas":[],"porcentajeGastado":40.0}`
- **Estado:** PASÓ

### CP-16 — Alerta del 70 %
- **Requisito / CA:** RF-09 / CA-04
- **Datos de entrada:** gastos acumulados de $700.000
- **Resultado esperado:** alerta de nivel 70 con sugerencia
- **Resultado obtenido:** alerta `nivel:70`, mensaje «Has alcanzado el 70% de tu presupuesto mensual.» y sugerencia «Llegaste al 70 % de tu gasto mensual. Te quedan $300000.00; revisa tus gastos.»
- **Estado:** PASÓ

### CP-18 — Sin alerta antes del 70 %
- **Requisito / CA:** CA-06
- **Datos de entrada:** gastos acumulados de $600.000
- **Resultado esperado:** `alertasActivas` vacía
- **Resultado obtenido:** `200` — `{"alertasActivas":[],"porcentajeGastado":60.0}`
- **Estado:** PASÓ

### CP-17 — Alerta del 80 % y 90 %
- **Requisito / CA:** CA-17 / CA-18
- **Datos de entrada:** gastos acumulados de $800.000 y luego $900.000 (presupuesto $1.000.000)
- **Resultado esperado:** una alerta del 80 % y luego una del 90 %
- **Resultado obtenido:** alerta `nivel:80` con sugerencia de $200.000 restantes; luego alerta `nivel:90` con sugerencia de $100.000 restantes
- **Estado:** PASÓ

### CP-19 — La alerta no se repite
- **Requisito / CA:** CA-19
- **Datos de entrada:** ya salió la del 70 %; se registran $30.000 más (73 %)
- **Resultado esperado:** no se crea otra alerta del 70 %
- **Resultado obtenido:** `porcentajeGastado:73.0` y una sola alerta `nivel:70`
- **Estado:** PASÓ

### CP-20 — Reinicio mensual
- **Requisito / CA:** CA-20
- **Datos de entrada:** en octubre salió la del 70 %; en noviembre llega otra vez al 70 %
- **Resultado esperado:** sale de nuevo la alerta del 70 %
- **Resultado obtenido:** no ejecutable por API con la fecha actual, porque el mes del presupuesto se toma de `LocalDate.now()` y el contrato no recibe mes/año. La lógica está cubierta por la consulta `existsByUsuarioIdAndNivelAndMesAndAnio` (la alerta se reinicia al cambiar `mes`/`anio`).
- **Estado:** N/A (pendiente de prueba manual con cambio de fecha del sistema)

### CP-21 — La alerta trae sugerencia
- **Requisito / CA:** RN-06
- **Datos de entrada:** cualquier alerta generada
- **Resultado esperado:** el mensaje incluye una sugerencia
- **Resultado obtenido:** las 3 alertas (70, 80 y 90) incluyen el campo `sugerencia`
- **Estado:** PASÓ

### CP-22 — Consultar sin iniciar sesión
- **Requisito / CA:** Seguridad
- **Datos de entrada:** `GET /api/presupuestos/alertas` sin credenciales
- **Resultado esperado:** 401
- **Resultado obtenido:** `401` — `{"message":"Debe iniciar sesión con credenciales válidas para usar el servicio"}`
- **Estado:** PASÓ

### CP-23 — Un usuario no ve datos de otro
- **Requisito / CA:** RNF-03
- **Datos de entrada:** usuario B consulta sus alertas
- **Resultado esperado:** no se muestran los datos del usuario A
- **Resultado obtenido:** `200` — `{"alertasActivas":[],"porcentajeGastado":0.0}`
- **Estado:** PASÓ

### CP-24 — Regresión del Sprint 1
- **Requisito / CA:** Regresión
- **Datos de entrada:** CP-01 a CP-12
- **Resultado esperado:** mismo resultado que en el Sprint 1
- **Resultado obtenido:** `GET /api/categorias` 200 con las 5 categorías; gasto con monto 0 → 400; categoría inválida → 422; `GET /api/gastos?mes=2026-11` 200 con 6 gastos y total $900.000
- **Estado:** PASÓ

## Nota sobre CP-20

El contrato actual de `POST /api/presupuestos` solo recibe `monto`; el mes y el año se toman de la fecha del servidor. Para probar el reinicio mensual se necesita cambiar la fecha del sistema o agregar un parámetro de mes/año al contrato. Se deja registrado como pendiente de prueba manual y no como fallo del Sprint 2.

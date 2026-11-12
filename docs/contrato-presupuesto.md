# Contrato REST – Presupuesto y alertas (CFP-31)

## POST /api/presupuestos – Definir el gasto mensual (RF-07)
- Entrada (JSON): `monto` (mayor que 0). El mes y el año son los actuales; el usuario sale de la sesión.
- Éxito: 201 con `mes`, `anio` y `montoLimite`. Si ya existía para el mes, se actualiza.
- Error: 400 si el monto es 0 o negativo; 401 sin sesión.

## GET /api/presupuestos/alertas – Porcentaje y alertas (RF-08, RF-09)
- Sin entrada: usa el usuario de la sesión y el mes actual.
- Éxito: 200 con `porcentajeGastado` y `alertasActivas` (nivel, mes, anio, mensaje, sugerencia, fechaCreacion).
- Regla: alerta al 70, 80 y 90 %; no se repite en el mismo nivel del mes; trae sugerencia (RN-04, RN-06).
- Error: 401 sin sesión.
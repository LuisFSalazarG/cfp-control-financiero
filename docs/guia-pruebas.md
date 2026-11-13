# Guía para la Ejecución de Pruebas Manuales (Calidad)

## 1. Preparación del entorno y usuarios de prueba
1. **Base de datos:** Ejecutar los scripts en MySQL Workbench en este orden: `db/reset.sql` → `db/schema.sql` → `db/migraciones/V2__presupuesto_y_alertas.sql`.
2. **Backend:** Arrancar el servidor localmente en el puerto `8080`.
3. **Usuarios de prueba:** El sistema identifica al usuario automáticamente mediante la sesión:
   - **Usuario A:** `a@ejemplo.invalid`
   - **Usuario B:** `b@ejemplo.invalid`

---

## 2. Flujo de pruebas del Sprint 2 (CP-13 a CP-23)

Ejecuta los pasos en este orden exacto para validar el funcionamiento del presupuesto mensual y las alertas:

### Paso 1: Definir el gasto mensual (`POST /api/presupuestos`)
El mes y el año los asigna el sistema automáticamente con la fecha actual.

```bash
# CP-13: Definir gasto mensual válido -> Esperado: 201 Created con mes, anio y montoLimite
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/presupuestos \
  -H "Content-Type: application/json" \
  -d '{"monto": 1000000}'

# CP-14: Gasto mensual en 0 -> Esperado: 400 Bad Request y no se guarda
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/presupuestos \
  -H "Content-Type: application/json" \
  -d '{"monto": 0}'


### Paso 2: Registrar gastos (POST /api/gastos)
Registra los montos acumulados para evaluar los porcentajes y los umbrales de alertas.

# CP-15: Gasto de $400.000 (sobre presupuesto de $1.000.000) -> Para validar porcentajeGastado = 40
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 400000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba porcentaje 40%"}'

# CP-18: Gasto adicional de $200.000 (acumulado $600.000 / 60%) -> Para validar alertasActivas vacía
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 200000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba sin alerta 60%"}'

# CP-16: Gasto adicional de $100.000 (acumulado $700.000 / 70%) -> Dispara alerta nivel 70 con sugerencia
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 100000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba alerta 70%"}'

# CP-19: Gasto adicional de $30.000 (acumulado $730.000 / 73%) -> No repite la alerta del 70%
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 30000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba no repeticion 73%"}'

# CP-17: Gastos adicionales hasta $800.000 y luego $900.000 -> Dispara alertas de 80% y 90%
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 70000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba alerta 80%"}'

curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 100000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba alerta 90%"}'

### Paso 3: Consultar presupuesto y alertas (GET /api/presupuestos/alertas)
El usuario se obtiene automáticamente de la sesión[cite: 3]. Verifica porcentajeGastado, alertasActivas y que cada alerta incluya su sugerencia (CP-21

# Consultar estado y alertas del Usuario A -> Esperado: 200 OK
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X GET http://localhost:8080/api/presupuestos/alertas \
  -H "Content-Type: application/json"

## 3. Pruebas de Seguridad, Privacidad y Regresión
Seguridad: Consultar sin iniciar sesión (CP-22)

# Esperado: Código 401 Unauthorized
curl -i -X GET http://localhost:8080/api/presupuestos/alertas \
  -H "Content-Type: application/json"

curl -i -u b@ejemplo.invalid:<CONTRASEÑA> -X GET http://localhost:8080/api/presupuestos/alertas \
  -H "Content-Type: application/json"

# CP-11: Consultar categorías -> Esperado: 200 OK con las 5 categorías
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X GET http://localhost:8080/api/categorias

# CP-02: Registrar gasto con monto en 0 -> Esperado: 400 Bad Request
curl -i -u a@ejemplo.invalid:<CONTRASEÑA> -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 0, "categoriaId": 1, "fecha": "2026-11-12"}'

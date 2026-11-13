# Guía para la Ejecución de Pruebas Manuales (Calidad)

## 1. Preparación del entorno y usuarios de prueba

1. **Base de datos:** Ejecutar los scripts en MySQL Workbench en este orden: `db/reset.sql` → `db/schema.sql` → `db/migraciones/V2__presupuesto_y_alertas.sql`.
2. **Backend:** Arrancar el servidor localmente en el puerto `8080`.
3. **Usuarios de prueba:** El sistema identifica al usuario automáticamente mediante la sesión:
   - **Usuario A:** `a@ejemplo.invalid`
   - **Usuario B:** `b@ejemplo.invalid`

**Nota:** Reemplazar `<CONTRASEÑA>` por la contraseña del usuario de prueba al ejecutar los comandos. No publicar credenciales reales en el repositorio.

---

## 2. Flujo de pruebas del Sprint 2 (CP-13 a CP-23)

Ejecutar los pasos en el orden indicado para validar el funcionamiento del presupuesto mensual y las alertas.

### Paso 1: Definir el gasto mensual (`POST /api/presupuestos`)

El mes y el año los asigna el sistema automáticamente con la fecha actual.

**CP-13: Definir gasto mensual válido**

Resultado esperado: `201 Created` con `mes`, `anio` y `montoLimite`.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/presupuestos \
  -H "Content-Type: application/json" \
  -d '{"monto": 1000000}'
```

**CP-14: Gasto mensual en 0**

Resultado esperado: `400 Bad Request` y no se guarda el presupuesto.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/presupuestos \
  -H "Content-Type: application/json" \
  -d '{"monto": 0}'
```

### Paso 2: Registrar gastos (`POST /api/gastos`)

Registrar los montos acumulados para evaluar los porcentajes y los umbrales de alertas.

**CP-15: Gasto de $400.000 sobre presupuesto de $1.000.000**

Resultado esperado: `porcentajeGastado = 40`.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 400000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba porcentaje 40%"}'
```

**CP-18: Gasto adicional de $200.000**

Acumulado: $600.000, equivalente al 60 %.

Resultado esperado: `alertasActivas` vacía.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 200000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba sin alerta 60%"}'
```

**CP-16: Gasto adicional de $100.000**

Acumulado: $700.000, equivalente al 70 %.

Resultado esperado: Se genera una alerta de nivel 70 con una sugerencia.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 100000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba alerta 70%"}'
```

**CP-19: Gasto adicional de $30.000**

Acumulado: $730.000, equivalente al 73 %.

Resultado esperado: No se genera nuevamente la alerta del 70 %.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 30000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba no repeticion 73%"}'
```

**CP-17: Gastos adicionales hasta el 80 % y 90 %**

Resultado esperado: Se genera una alerta al alcanzar el 80 % y otra al alcanzar el 90 %.

Registrar gasto adicional de $70.000:

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 70000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba alerta 80%"}'
```

Registrar gasto adicional de $100.000:

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 100000, "categoriaId": 1, "fecha": "2026-11-12", "nota": "Prueba alerta 90%"}'
```

### Paso 3: Consultar presupuesto y alertas (`GET /api/presupuestos/alertas`)

El usuario se obtiene automáticamente de la sesión.

Verificar los siguientes aspectos:

- `porcentajeGastado`.
- `alertasActivas`.
- Que cada alerta incluya una sugerencia (CP-21).
- Que no existan alertas duplicadas para el mismo nivel y mes.

**Consultar estado y alertas del Usuario A**

Resultado esperado: `200 OK`.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X GET http://localhost:8080/api/presupuestos/alertas \
  -H "Content-Type: application/json"
```

---

## 3. Pruebas de Seguridad, Privacidad y Regresión

### 3.1. Seguridad: Consultar sin iniciar sesión (CP-22)

Verificar que no se permita consultar las alertas sin autenticación.

Resultado esperado: `401 Unauthorized`.

```bash
curl -i -X GET http://localhost:8080/api/presupuestos/alertas \
  -H "Content-Type: application/json"
```

### 3.2. Privacidad: Aislamiento de datos entre usuarios (CP-23 - RNF-03)

El Usuario B consulta `/api/presupuestos/alertas` con sus propias credenciales.

Resultado esperado: El Usuario B solo debe visualizar su información, nunca las alertas ni los presupuestos del Usuario A.

```bash
curl -i -u 'b@ejemplo.invalid:<CONTRASEÑA>' -X GET http://localhost:8080/api/presupuestos/alertas \
  -H "Content-Type: application/json"
```

### 3.3. Regresión del Sprint 1 (CP-24)

Confirmar que los casos críticos del Sprint 1 siguen funcionando con el mismo resultado esperado.

**CP-11: Consultar categorías**

Resultado esperado: `200 OK` con las cinco categorías.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X GET http://localhost:8080/api/categorias
```

**CP-02: Registrar gasto con monto en 0**

Resultado esperado: `400 Bad Request`.

```bash
curl -i -u 'a@ejemplo.invalid:<CONTRASEÑA>' -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{"monto": 0, "categoriaId": 1, "fecha": "2026-11-12"}'
```

---

## 4. Registro de evidencias

Cada caso de prueba ejecutado debe documentarse utilizando la plantilla `docs/plantilla-evidencia.md`.

Registrar:

- Identificador del caso de prueba.
- Requisito o criterio de aceptación.
- Fecha de ejecución.
- Responsable de la prueba.
- Datos de entrada.
- Resultado esperado.
- Resultado obtenido.
- Estado: PASÓ / FALLÓ.
- Captura de pantalla correspondiente.

Los resultados deberán utilizarse como evidencia del proceso de Calidad e Integración del Sprint 2.

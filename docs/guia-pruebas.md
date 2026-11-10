# Guía para la Ejecución de Pruebas Manuales (Calidad)

## 1. Pruebas del Endpoint de Presupuesto y Alertas (Sprint 2)

### Probar consulta de alertas del usuario
curl -X GET http://localhost:8080/api/presupuestos/alertas/1 -H "Content-Type: application/json"

### Probar registro de presupuesto mensual
curl -X POST http://localhost:8080/api/presupuestos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": 1, "monto": 1000000}'

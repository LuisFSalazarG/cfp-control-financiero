# Guía para la ejecución de pruebas manuales (Calidad)
 
## 0. Antes de empezar
1. Crear la base: ejecutar db/schema.sql y luego db/migraciones/V2__presupuesto_y_alertas.sql en MySQL Workbench.
2. Arrancar el backend desde la carpeta backend:
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.password=TU_CONTRASEÑA"
3. Usuarios de prueba: a@ejemplo.invalid y b@ejemplo.invalid, contraseña: password.
4. Los comandos se ejecutan en Git Bash. Cada resultado se registra con docs/plantilla-evidencia.md.
 
## 1. Registrar gastos (para llegar al 70, 80 y 90 %)
curl -u a@ejemplo.invalid:password -X POST http://localhost:8080/api/gastos -H "Content-Type: application/json" -d '{"monto": 700000, "categoria": "ALIMENTACION", "fecha": "2026-11-10"}'
 
## 2. Consultar porcentaje y alertas del usuario
curl -u a@ejemplo.invalid:password http://localhost:8080/api/presupuestos/alertas/1
 
## 3. Definir el gasto mensual (PENDIENTE: el backend aún no tiene este servicio, ver hallazgo H-03)
curl -u a@ejemplo.invalid:password -X POST http://localhost:8080/api/presupuestos -H "Content-Type: application/json" -d '{"mes": 11, "anio": 2026, "montoLimite": 1000000}'
 
## 4. Seguridad
- Sin iniciar sesión (debe dar 401): curl -i http://localhost:8080/api/presupuestos/alertas/1
- Usuario B pidiendo las alertas de A (no debe mostrarlas): curl -i -u b@ejemplo.invalid:password http://localhost:8080/api/presupuestos/alertas/1
Nota: los campos de /api/gastos (monto, categoria, fecha y nota) están explicados en docs/contrato-gastos.md.

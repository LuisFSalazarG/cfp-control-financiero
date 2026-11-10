-- CFP-30 | Pruebas de las tablas del Sprint 2 (MySQL Workbench)
-- Ejecutar DESPUES de db/migraciones/V2__presupuesto_y_alertas.sql.
-- Ejecutar cada INSERT por separado (Ctrl + Enter): los que deben fallar detienen el rayo.
-- Resultado obtenido el 09/11/2026: las 9 pruebas se comportaron como se esperaba.

USE cfp_dev;

-- 1. Valido: gasto mensual de noviembre. ESPERADO: se guarda.
INSERT INTO presupuesto (usuario_id, mes, anio, monto_limite) VALUES (1, 11, 2026, 1000000);
-- 2. Mismo usuario y mes. ESPERADO: Error 1062 Duplicate entry (uq_presupuesto_usuario_mes).
INSERT INTO presupuesto (usuario_id, mes, anio, monto_limite) VALUES (1, 11, 2026, 500000);
-- 3. Monto en 0. ESPERADO: Error 3819 ck_presupuesto_monto.
INSERT INTO presupuesto (usuario_id, mes, anio, monto_limite) VALUES (1, 12, 2026, 0);
-- 4. Mes 13. ESPERADO: Error 3819 ck_presupuesto_mes.
INSERT INTO presupuesto (usuario_id, mes, anio, monto_limite) VALUES (1, 13, 2026, 1000000);
-- 5. Valido: alerta del 70 %. ESPERADO: se guarda.
INSERT INTO alerta (usuario_id, tipo_alerta, fecha, nivel, mes, anio) VALUES (1, 'Llegaste al 70 %', '2026-11-09', 70, 11, 2026);
-- 6. La misma alerta en el mismo mes. ESPERADO: Error 1062 Duplicate entry (no se repite).
INSERT INTO alerta (usuario_id, tipo_alerta, fecha, nivel, mes, anio) VALUES (1, 'Llegaste al 70 %', '2026-11-09', 70, 11, 2026);
-- 7. Nivel 75. ESPERADO: Error 3819 ck_alerta_nivel (solo 70, 80 o 90).
INSERT INTO alerta (usuario_id, tipo_alerta, fecha, nivel, mes, anio) VALUES (1, 'Prueba', '2026-11-09', 75, 11, 2026);
-- 8. Usuario sin telefono. ESPERADO: Error 1048 Column 'telefono' cannot be null (RF-01).
INSERT INTO usuario (nombre_completo, correo, cedula, telefono, contrasena_hash) VALUES ('Sin telefono', 'x@ejemplo.invalid', '0000000009', NULL, 'x');
-- 9. Categoria inexistente. ESPERADO: Error 1452 foreign key constraint fails.
INSERT INTO presupuesto_categoria (usuario_id, categoria_id, mes, anio, monto_limite) VALUES (1, 99, 11, 2026, 200000);

-- Limpieza: borra los datos de prueba que si se guardaron (pruebas 1 y 5).
DELETE FROM alerta WHERE id > 0;
DELETE FROM presupuesto WHERE id > 0;
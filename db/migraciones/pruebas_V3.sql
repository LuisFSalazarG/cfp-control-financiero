-- CFP-35 | Pruebas de las tablas y vistas del Sprint 3 (MySQL Workbench)
-- Ejecutar DESPUES de db/migraciones/V3__ingresos_gastos_fijos_y_vistas.sql.
-- Ejecutar cada INSERT por separado (Ctrl + Enter): los que deben fallar detienen el rayo.

USE cfp_dev;

-- 1. Valido: ingreso de $2.500.000. ESPERADO: se guarda.
INSERT INTO ingreso (usuario_id, monto, fecha, nota) VALUES (1, 2500000, '2026-11-01', 'Salario');
-- 2. Ingreso en 0. ESPERADO: Error 3819 ck_ingreso_monto_positivo (RN-01).
INSERT INTO ingreso (usuario_id, monto, fecha, nota) VALUES (1, 0, '2026-11-01', 'Prueba');
-- 3. Ingreso sin fecha. ESPERADO: Error 1048 Column 'fecha' cannot be null (RN-01).
INSERT INTO ingreso (usuario_id, monto, fecha, nota) VALUES (1, 100000, NULL, 'Prueba');
-- 4. Ingreso de un usuario que no existe. ESPERADO: Error 1452 foreign key.
INSERT INTO ingreso (usuario_id, monto, fecha, nota) VALUES (999, 100000, '2026-11-01', 'Prueba');
-- 5. Valido: gasto fijo de arriendo, dia 5, categoria HOGAR. ESPERADO: se guarda.
INSERT INTO gasto_fijo (usuario_id, categoria_id, descripcion, monto, dia_mes)
VALUES (1, (SELECT id FROM categoria WHERE codigo = 'HOGAR'), 'Arriendo', 800000, 5);
-- 6. Mismo gasto fijo otra vez. ESPERADO: Error 1062 Duplicate entry (uq_gasto_fijo_usuario_descripcion).
INSERT INTO gasto_fijo (usuario_id, categoria_id, descripcion, monto, dia_mes)
VALUES (1, (SELECT id FROM categoria WHERE codigo = 'HOGAR'), 'Arriendo', 800000, 5);
-- 7. Gasto fijo el dia 31. ESPERADO: Error 3819 ck_gasto_fijo_dia (solo 1 a 28).
INSERT INTO gasto_fijo (usuario_id, categoria_id, descripcion, monto, dia_mes)
VALUES (1, (SELECT id FROM categoria WHERE codigo = 'HOGAR'), 'Internet', 90000, 31);
-- 8. Gasto fijo con monto negativo. ESPERADO: Error 3819 ck_gasto_fijo_monto_positivo.
INSERT INTO gasto_fijo (usuario_id, categoria_id, descripcion, monto, dia_mes)
VALUES (1, (SELECT id FROM categoria WHERE codigo = 'HOGAR'), 'Agua', -50000, 10);
-- 9. Gasto fijo con categoria que no existe. ESPERADO: Error 1452 foreign key.
INSERT INTO gasto_fijo (usuario_id, categoria_id, descripcion, monto, dia_mes)
VALUES (1, 99, 'Gimnasio', 120000, 15);
-- 10. Vista de movimientos. ESPERADO: aparece el ingreso de la prueba 1 con tipo INGRESO.
SELECT * FROM v_movimiento WHERE usuario_id = 1;

-- Limpieza: borra los datos que si se guardaron (pruebas 1 y 5).
DELETE FROM gasto_fijo WHERE id > 0;
DELETE FROM ingreso WHERE id > 0;

-- CFP-35 | Datos semilla para el Dashboard (solo DESARROLLO)
-- Ejecutar despues de la V3 y despues de arrancar el backend una vez con MySQL
-- (el backend crea los usuarios a@ejemplo.invalid y b@ejemplo.invalid).
-- Si a@ejemplo.invalid no existe, usa el usuario de prueba de schema.sql.
-- Carga datos del mes anterior y del mes actual para que las graficas tengan que mostrar.

USE cfp_dev;

SET @u := COALESCE(
    (SELECT id FROM usuario WHERE correo = 'a@ejemplo.invalid'),
    (SELECT id FROM usuario WHERE correo = 'pruebas@ejemplo.invalid'));
SET @alim := (SELECT id FROM categoria WHERE codigo = 'ALIMENTACION');
SET @trans := (SELECT id FROM categoria WHERE codigo = 'TRANSPORTE');
SET @entre := (SELECT id FROM categoria WHERE codigo = 'ENTRETENIMIENTO');
SET @hogar := (SELECT id FROM categoria WHERE codigo = 'HOGAR');
SET @otros := (SELECT id FROM categoria WHERE codigo = 'OTROS');
SET @mes_actual := DATE_FORMAT(CURDATE(), '%Y-%m-01');
SET @mes_anterior := DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01');

-- Ingresos
INSERT INTO ingreso (usuario_id, monto, fecha, nota) VALUES
    (@u, 2500000, @mes_anterior, 'Salario'),
    (@u, 300000, @mes_anterior + INTERVAL 14 DAY, 'Trabajo independiente'),
    (@u, 2500000, @mes_actual, 'Salario'),
    (@u, 150000, @mes_actual + INTERVAL 9 DAY, 'Venta de articulo');

-- Gastos del mes anterior
INSERT INTO gasto (usuario_id, categoria_id, monto, fecha, nota) VALUES
    (@u, @hogar, 800000, @mes_anterior + INTERVAL 4 DAY, 'Arriendo'),
    (@u, @alim, 450000, @mes_anterior + INTERVAL 7 DAY, 'Mercado'),
    (@u, @trans, 180000, @mes_anterior + INTERVAL 10 DAY, 'Transporte'),
    (@u, @entre, 220000, @mes_anterior + INTERVAL 15 DAY, 'Cine y salidas'),
    (@u, @otros, 90000, @mes_anterior + INTERVAL 20 DAY, 'Varios');

-- Gastos del mes actual
INSERT INTO gasto (usuario_id, categoria_id, monto, fecha, nota) VALUES
    (@u, @hogar, 800000, @mes_actual + INTERVAL 4 DAY, 'Arriendo'),
    (@u, @alim, 380000, @mes_actual + INTERVAL 6 DAY, 'Mercado'),
    (@u, @trans, 120000, @mes_actual + INTERVAL 8 DAY, 'Transporte'),
    (@u, @entre, 150000, @mes_actual + INTERVAL 11 DAY, 'Concierto');

-- Gastos fijos
INSERT INTO gasto_fijo (usuario_id, categoria_id, descripcion, monto, dia_mes) VALUES
    (@u, @hogar, 'Arriendo', 800000, 5),
    (@u, @hogar, 'Internet', 90000, 10),
    (@u, @entre, 'Suscripcion de streaming', 35000, 15);

-- Verificacion
SELECT * FROM v_resumen_mensual WHERE usuario_id = @u ORDER BY anio, mes;
SELECT * FROM v_gasto_por_categoria WHERE usuario_id = @u ORDER BY anio, mes, total DESC;

-- CFP-35 | Consultas para los servicios del Dashboard y de movimientos
-- Referencia para Backend (CFP-36) y Calidad (CFP-37). Cambiar los valores de @u, @desde y @hasta.

USE cfp_dev;

SET @u := (SELECT id FROM usuario WHERE correo = 'a@ejemplo.invalid');
SET @desde := '2026-11-01';
SET @hasta := '2026-11-30';

-- RF-11 / CFP-14: movimientos del usuario entre dos fechas (mas recientes primero).
SELECT tipo, id, fecha, monto, categoria, nota
FROM v_movimiento
WHERE usuario_id = @u AND fecha BETWEEN @desde AND @hasta
ORDER BY fecha DESC, tipo;

-- RF-12 / CFP-19: ingresos vs. egresos por mes.
SELECT anio, mes, total_ingresos, total_gastos, balance
FROM v_resumen_mensual
WHERE usuario_id = @u
ORDER BY anio, mes;

-- RF-13 / CFP-20: peso de cada categoria en los gastos de un mes.
SELECT categoria, total, porcentaje
FROM v_gasto_por_categoria
WHERE usuario_id = @u AND anio = YEAR(@desde) AND mes = MONTH(@desde)
ORDER BY total DESC;

-- RF-14 / CFP-21: ingresos vs. egresos en un rango de fechas cualquiera.
SELECT SUM(CASE WHEN tipo = 'INGRESO' THEN monto ELSE 0 END) AS total_ingresos,
       SUM(CASE WHEN tipo = 'GASTO' THEN monto ELSE 0 END) AS total_gastos
FROM v_movimiento
WHERE usuario_id = @u AND fecha BETWEEN @desde AND @hasta;

-- RF-06 / CFP-22: gastos fijos activos del usuario.
SELECT gf.descripcion, c.codigo AS categoria, gf.monto, gf.dia_mes
FROM gasto_fijo gf
JOIN categoria c ON c.id = gf.categoria_id
WHERE gf.usuario_id = @u AND gf.activo = TRUE
ORDER BY gf.dia_mes;

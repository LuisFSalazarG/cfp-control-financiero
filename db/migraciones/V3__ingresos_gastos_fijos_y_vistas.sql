-- CFP-35 | Control Financiero Personal | Sprint 3
-- Motor: MySQL 8.0.16 o posterior. Ejecutar UNA SOLA VEZ, despues de V2 (y de V2_1 si ya esta aplicada).
-- Agrega:
--   * ingreso     -> registrar ingresos (RF-02, CFP-13)
--   * gasto_fijo  -> gastos fijos mensuales (RF-06, CFP-22)
--   * vistas para movimientos (RF-11) y para los servicios del Dashboard (RF-12, RF-13, RF-14)
-- Los nombres de tablas y columnas son los que debe usar Backend (CFP-36) en sus entidades.

USE cfp_dev;

-- RF-02: ingreso del usuario. Misma estructura que gasto, pero sin categoria
-- (las cinco categorias son solo para gastos).
CREATE TABLE ingreso (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    monto BIGINT NOT NULL,
    fecha DATE NOT NULL,
    nota VARCHAR(200) NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT ck_ingreso_monto_positivo CHECK (monto > 0),
    CONSTRAINT fk_ingreso_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    INDEX idx_ingreso_usuario_fecha (usuario_id, fecha)
) ENGINE=InnoDB;

-- RF-06: gasto fijo mensual (arriendo, servicios, suscripciones...).
-- dia_mes de 1 a 28 para que exista en todos los meses, incluido febrero.
-- Un usuario no puede repetir la misma descripcion de gasto fijo.
CREATE TABLE gasto_fijo (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    categoria_id BIGINT UNSIGNED NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    monto BIGINT NOT NULL,
    dia_mes TINYINT UNSIGNED NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT ck_gasto_fijo_monto_positivo CHECK (monto > 0),
    CONSTRAINT ck_gasto_fijo_dia CHECK (dia_mes BETWEEN 1 AND 28),
    CONSTRAINT uq_gasto_fijo_usuario_descripcion UNIQUE (usuario_id, descripcion),
    CONSTRAINT fk_gasto_fijo_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_gasto_fijo_categoria FOREIGN KEY (categoria_id)
        REFERENCES categoria(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- RF-11: todos los movimientos (ingresos y gastos) en una sola lista.
-- Se filtra por usuario_id y por rango de fechas.
CREATE VIEW v_movimiento AS
    SELECT 'INGRESO' AS tipo, i.id, i.usuario_id, i.fecha, i.monto,
           NULL AS categoria, i.nota
    FROM ingreso i
    UNION ALL
    SELECT 'GASTO' AS tipo, g.id, g.usuario_id, g.fecha, g.monto,
           c.codigo AS categoria, g.nota
    FROM gasto g
    JOIN categoria c ON c.id = g.categoria_id;

-- RF-12: ingresos vs. egresos por usuario y mes.
CREATE VIEW v_resumen_mensual AS
    SELECT usuario_id,
           YEAR(fecha) AS anio,
           MONTH(fecha) AS mes,
           SUM(CASE WHEN tipo = 'INGRESO' THEN monto ELSE 0 END) AS total_ingresos,
           SUM(CASE WHEN tipo = 'GASTO' THEN monto ELSE 0 END) AS total_gastos,
           SUM(CASE WHEN tipo = 'INGRESO' THEN monto ELSE -monto END) AS balance
    FROM v_movimiento
    GROUP BY usuario_id, YEAR(fecha), MONTH(fecha);

-- RF-13: peso de cada categoria en los gastos del mes (total y porcentaje).
CREATE VIEW v_gasto_por_categoria AS
    SELECT g.usuario_id,
           YEAR(g.fecha) AS anio,
           MONTH(g.fecha) AS mes,
           c.codigo AS categoria,
           SUM(g.monto) AS total,
           ROUND(100 * SUM(g.monto)
                 / SUM(SUM(g.monto)) OVER (PARTITION BY g.usuario_id, YEAR(g.fecha), MONTH(g.fecha)), 2)
               AS porcentaje
    FROM gasto g
    JOIN categoria c ON c.id = g.categoria_id
    GROUP BY g.usuario_id, YEAR(g.fecha), MONTH(g.fecha), c.codigo;

-- Verificacion: deben aparecer 8 tablas y 3 vistas.
SHOW FULL TABLES;

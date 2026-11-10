-- CFP-30 | Control Financiero Personal | Sprint 2
-- Motor: MySQL 8.0.16 o posterior. Ejecutar UNA SOLA VEZ, despues de db/schema.sql.
-- Agrega el gasto mensual (presupuesto), el gasto por categoria y las alertas,
-- y deja el telefono del usuario como obligatorio (RF-01).
-- Los nombres coinciden con las entidades del backend (CFP-31).

USE cfp_dev;

-- RF-01: telefono obligatorio. Primero se completa el usuario de prueba que no lo tenia.
UPDATE usuario SET telefono = '3000000000' WHERE id > 0 AND telefono IS NULL;
ALTER TABLE usuario MODIFY telefono VARCHAR(20) NOT NULL;

-- RF-07: gasto mensual del usuario (uno por usuario y mes).
CREATE TABLE presupuesto (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    mes INT NOT NULL,
    anio INT NOT NULL,
    monto_limite DECIMAL(15,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_presupuesto_mes CHECK (mes BETWEEN 1 AND 12),
    CONSTRAINT ck_presupuesto_anio CHECK (anio BETWEEN 2000 AND 2100),
    CONSTRAINT ck_presupuesto_monto CHECK (monto_limite > 0),
    CONSTRAINT uq_presupuesto_usuario_mes UNIQUE (usuario_id, anio, mes),
    CONSTRAINT fk_presupuesto_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- RF-07: gasto fijado para cada categoria (uno por usuario, categoria y mes).
CREATE TABLE presupuesto_categoria (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    categoria_id BIGINT UNSIGNED NOT NULL,
    mes INT NOT NULL,
    anio INT NOT NULL,
    monto_limite DECIMAL(15,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_prescat_mes CHECK (mes BETWEEN 1 AND 12),
    CONSTRAINT ck_prescat_anio CHECK (anio BETWEEN 2000 AND 2100),
    CONSTRAINT ck_prescat_monto CHECK (monto_limite > 0),
    CONSTRAINT uq_prescat_usuario_cat_mes UNIQUE (usuario_id, categoria_id, anio, mes),
    CONSTRAINT fk_prescat_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_prescat_categoria FOREIGN KEY (categoria_id)
        REFERENCES categoria(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- RF-09: alertas al 70, 80 y 90 %.
-- nivel, mes y anio sirven para que la misma alerta no se repita en el mes.
-- Quedan opcionales hasta que Backend los use (coordinado con CFP-31).
CREATE TABLE alerta (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    tipo_alerta VARCHAR(255) NOT NULL,
    fecha DATE NOT NULL,
    nivel TINYINT UNSIGNED NULL,
    mes INT NULL,
    anio INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_alerta_nivel CHECK (nivel IS NULL OR nivel IN (70, 80, 90)),
    CONSTRAINT ck_alerta_mes CHECK (mes IS NULL OR mes BETWEEN 1 AND 12),
    CONSTRAINT uq_alerta_usuario_mes_nivel UNIQUE (usuario_id, anio, mes, nivel),
    CONSTRAINT fk_alerta_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    INDEX idx_alerta_usuario_fecha (usuario_id, fecha)
) ENGINE=InnoDB;

-- Verificacion: deben aparecer 6 tablas y el usuario de prueba con telefono.
SHOW TABLES;
SELECT id, correo, telefono FROM usuario;

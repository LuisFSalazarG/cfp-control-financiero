-- CFP-25 | Control Financiero Personal | Sprint 1
-- Motor: MySQL Server 8.0.16 o posterior (cliente: MySQL Workbench 8.0 CE)
-- Uso: ejecutar TODO el archivo UNA SOLA VEZ en un entorno de DESARROLLO sin tablas CFP existentes.
-- No ejecutarlo sobre produccion. Las operaciones DDL no se revierten con ROLLBACK en MySQL.
-- Alcance: usuario, categoria y gasto. Sin ingresos, presupuestos ni alertas.

CREATE DATABASE IF NOT EXISTS cfp_dev
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE cfp_dev;

CREATE TABLE usuario (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre_completo VARCHAR(150) NOT NULL,
    correo VARCHAR(254) NOT NULL,
    cedula VARCHAR(20) NOT NULL,
    telefono VARCHAR(20) NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_usuario_correo UNIQUE (correo),
    CONSTRAINT uq_usuario_cedula UNIQUE (cedula)
) ENGINE=InnoDB;

CREATE TABLE categoria (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_categoria_codigo UNIQUE (codigo),
    CONSTRAINT ck_categoria_codigo CHECK (
        codigo IN ('ALIMENTACION','TRANSPORTE','ENTRETENIMIENTO','HOGAR','OTROS')
    )
) ENGINE=InnoDB;

CREATE TABLE gasto (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT UNSIGNED NOT NULL,
    categoria_id BIGINT UNSIGNED NOT NULL,
    monto BIGINT NOT NULL,
    fecha DATE NOT NULL,
    nota VARCHAR(200) NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT ck_gasto_monto_positivo CHECK (monto > 0),
    CONSTRAINT fk_gasto_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_gasto_categoria FOREIGN KEY (categoria_id)
        REFERENCES categoria(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    INDEX idx_gasto_usuario_fecha (usuario_id, fecha)
) ENGINE=InnoDB;

-- RN-03: catalogo fijo de cinco categorias (sin poder agregar, editar o borrar otras).
INSERT INTO categoria (codigo, nombre) VALUES
    ('ALIMENTACION', 'Alimentación'),
    ('TRANSPORTE', 'Transporte'),
    ('ENTRETENIMIENTO', 'Entretenimiento'),
    ('HOGAR', 'Hogar'),
    ('OTROS', 'Otros');

-- En MySQL los triggers se pueden expresar mediante una sola instruccion SIGNAL.
CREATE TRIGGER trg_categoria_bloquear_insert
BEFORE INSERT ON categoria FOR EACH ROW
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Las categorias son fijas y no se pueden modificar';

CREATE TRIGGER trg_categoria_bloquear_update
BEFORE UPDATE ON categoria FOR EACH ROW
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Las categorias son fijas y no se pueden modificar';

CREATE TRIGGER trg_categoria_bloquear_delete
BEFORE DELETE ON categoria FOR EACH ROW
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Las categorias son fijas y no se pueden modificar';

-- Usuario ficticio exclusivamente para demostrar CFP-25 antes de implementar RF-01.
-- contrasena_hash es un hash de prueba: no compartir credenciales ni usar en produccion.
INSERT INTO usuario (nombre_completo, correo, cedula, telefono, contrasena_hash)
VALUES (
    'Usuario de prueba CFP',
    'pruebas@ejemplo.invalid',
    '0000000000',
    NULL,
    '$2a$10$HaS2nS5LU48L37i.iZws6e.oib7JM5aaw90jbcKgyQiXGYYi84Kq2'
);

-- Verificacion inicial: deben aparecer tres tablas y cinco categorias.
SHOW TABLES;
SELECT id, codigo, nombre FROM categoria ORDER BY id;
SELECT VERSION() AS version_servidor_mysql, DATABASE() AS base_activa;

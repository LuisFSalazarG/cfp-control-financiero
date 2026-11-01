-- CFP-25 | Pruebas de integridad MySQL 8.0.16+ (compatible con Workbench 8.0)
-- EJECUTAR despues de db/schema.sql. Requiere permiso CREATE ROUTINE.
-- Las inserciones de prueba estan dentro de una transaccion y se revierten.
-- Esta verificacion NO ejecuta pruebas HTTP de los endpoints del backend.

USE cfp_dev;

DROP PROCEDURE IF EXISTS sp_cfp25_verificar;
DELIMITER $$
CREATE PROCEDURE sp_cfp25_verificar()
BEGIN
    DECLARE v_usuario_id BIGINT UNSIGNED;
    DECLARE v_categoria_id BIGINT UNSIGNED;
    DECLARE v_total_inicial BIGINT DEFAULT 0;
    DECLARE v_total_despues BIGINT DEFAULT 0;
    DECLARE v_cantidad_inicial BIGINT DEFAULT 0;
    DECLARE v_cantidad_tras_valido BIGINT DEFAULT 0;
    DECLARE v_cantidad_final BIGINT DEFAULT 0;
    DECLARE v_numero_categorias BIGINT DEFAULT 0;
    DECLARE v_rechazado BOOLEAN DEFAULT FALSE;
    DECLARE v_resultados INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT MIN(id) INTO v_usuario_id FROM usuario WHERE correo = 'pruebas@ejemplo.invalid';
    SELECT MIN(id) INTO v_categoria_id FROM categoria WHERE codigo = 'ALIMENTACION';
    IF v_usuario_id IS NULL OR v_categoria_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Falta el usuario o la categoria de prueba. Ejecute schema.sql primero.';
    END IF;

    SELECT COUNT(*) INTO v_numero_categorias FROM categoria;
    IF v_numero_categorias <> 5 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: deben existir exactamente 5 categorias';
    END IF;
    SET v_resultados = v_resultados + 1;

    START TRANSACTION;

    SELECT COUNT(*), COALESCE(SUM(monto), 0)
      INTO v_cantidad_inicial, v_total_inicial
      FROM gasto WHERE usuario_id = v_usuario_id
        AND fecha >= '2026-10-01' AND fecha < '2026-11-01';

    -- Prueba: gasto valido. Debe sumar COP 50.000 al total mensual.
    INSERT INTO gasto (usuario_id, categoria_id, monto, fecha, nota)
    VALUES (v_usuario_id, v_categoria_id, 50000, '2026-10-16', 'Mercado de prueba');

    SELECT COUNT(*), COALESCE(SUM(monto), 0)
      INTO v_cantidad_tras_valido, v_total_despues
      FROM gasto WHERE usuario_id = v_usuario_id
        AND fecha >= '2026-10-01' AND fecha < '2026-11-01';

    IF v_cantidad_tras_valido <> v_cantidad_inicial + 1
        OR v_total_despues <> v_total_inicial + 50000 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: gasto valido o total mensual incorrecto';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: monto = 0 -> violacion de CHECK (3819).
    SET v_rechazado = FALSE;
    BEGIN
        DECLARE CONTINUE HANDLER FOR 3819 SET v_rechazado = TRUE;
        INSERT INTO gasto (usuario_id, categoria_id, monto, fecha)
        VALUES (v_usuario_id, v_categoria_id, 0, '2026-10-16');
    END;
    IF NOT v_rechazado THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: se acepto monto cero';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: monto negativo -> violacion de CHECK (3819).
    SET v_rechazado = FALSE;
    BEGIN
        DECLARE CONTINUE HANDLER FOR 3819 SET v_rechazado = TRUE;
        INSERT INTO gasto (usuario_id, categoria_id, monto, fecha)
        VALUES (v_usuario_id, v_categoria_id, -5000, '2026-10-16');
    END;
    IF NOT v_rechazado THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: se acepto monto negativo';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: categoria obligatoria -> NOT NULL (1048).
    SET v_rechazado = FALSE;
    BEGIN
        DECLARE CONTINUE HANDLER FOR 1048 SET v_rechazado = TRUE;
        INSERT INTO gasto (usuario_id, categoria_id, monto, fecha)
        VALUES (v_usuario_id, NULL, 50000, '2026-10-16');
    END;
    IF NOT v_rechazado THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: se acepto categoria NULL';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: categoria que no existe -> FK (1452).
    SET v_rechazado = FALSE;
    BEGIN
        DECLARE CONTINUE HANDLER FOR 1452 SET v_rechazado = TRUE;
        INSERT INTO gasto (usuario_id, categoria_id, monto, fecha)
        VALUES (v_usuario_id, 999999999, 50000, '2026-10-16');
    END;
    IF NOT v_rechazado THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: se acepto categoria inexistente';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: fecha obligatoria -> NOT NULL (1048).
    SET v_rechazado = FALSE;
    BEGIN
        DECLARE CONTINUE HANDLER FOR 1048 SET v_rechazado = TRUE;
        INSERT INTO gasto (usuario_id, categoria_id, monto, fecha)
        VALUES (v_usuario_id, v_categoria_id, 50000, NULL);
    END;
    IF NOT v_rechazado THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: se acepto fecha NULL';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: no se pueden agregar nuevas categorias -> SIGNAL 45000.
    SET v_rechazado = FALSE;
    BEGIN
        DECLARE CONTINUE HANDLER FOR SQLSTATE '45000' SET v_rechazado = TRUE;
        INSERT INTO categoria (codigo, nombre) VALUES ('MASCOTAS', 'Mascotas');
    END;
    IF NOT v_rechazado THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: se permitio insertar otra categoria';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: no se pueden editar categorias.
    SET v_rechazado = FALSE;
    BEGIN
        DECLARE CONTINUE HANDLER FOR SQLSTATE '45000' SET v_rechazado = TRUE;
        UPDATE categoria SET nombre = 'Editada' WHERE id = v_categoria_id;
    END;
    IF NOT v_rechazado THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: se permitio editar una categoria';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: no se pueden borrar categorias.
    SET v_rechazado = FALSE;
    BEGIN
        DECLARE CONTINUE HANDLER FOR SQLSTATE '45000' SET v_rechazado = TRUE;
        DELETE FROM categoria WHERE id = v_categoria_id;
    END;
    IF NOT v_rechazado THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: se permitio borrar una categoria';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Prueba: ningun rechazo cambio la cantidad de gastos o el total.
    SELECT COUNT(*), COALESCE(SUM(monto), 0)
      INTO v_cantidad_final, v_total_despues
      FROM gasto WHERE usuario_id = v_usuario_id
        AND fecha >= '2026-10-01' AND fecha < '2026-11-01';
    IF v_cantidad_final <> v_cantidad_tras_valido
        OR v_total_despues <> v_total_inicial + 50000 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: un rechazo modifico los gastos o el total';
    END IF;
    SET v_resultados = v_resultados + 1;

    -- Revertir el gasto valido: la base queda igual que antes de las pruebas.
    ROLLBACK;

    SELECT COUNT(*), COALESCE(SUM(monto), 0)
      INTO v_cantidad_final, v_total_despues
      FROM gasto WHERE usuario_id = v_usuario_id
        AND fecha >= '2026-10-01' AND fecha < '2026-11-01';
    IF v_cantidad_final <> v_cantidad_inicial OR v_total_despues <> v_total_inicial THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'FALLO: el ROLLBACK no restauro los valores iniciales';
    END IF;
    SET v_resultados = v_resultados + 1;

    SELECT 'APROBADO' AS estado,
           v_resultados AS pruebas_superadas,
           'Integridad, catalogo fijo, totales y rollback verificados' AS resumen,
           'Solo pruebas de base de datos; pruebas REST pendientes de Backend/Calidad' AS alcance;
END$$
DELIMITER ;

CALL sp_cfp25_verificar();
DROP PROCEDURE IF EXISTS sp_cfp25_verificar;

-- CFP-18. Aplicar una vez después de V2__presupuesto_y_alertas.sql.
USE cfp_dev;

ALTER TABLE presupuesto_categoria
    ADD COLUMN alerta_70_emitida BOOLEAN NOT NULL DEFAULT FALSE;

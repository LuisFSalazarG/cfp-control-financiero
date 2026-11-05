# Modelo de datos – Sprint 1

Responsable actual: Luis Fernando Salazar (Datos) · Subtarea original: CFP-25
Motor: MySQL 8 · Scripts: db/schema.sql y db/pruebas.sql

## Entidades

| Entidad | Atributos | Estados | Relaciones | Restricción / RN |
|---|---|---|---|---|
| Usuario | id, nombre_completo, correo, cedula, telefono, contrasena_hash | No aplica | Tiene muchos gastos | Correo y cédula únicos; contraseña como hash (RNF-07); cada usuario solo ve sus datos (RNF-03) |
| Categoría | id, codigo, nombre | Catálogo fijo de 5 | Tiene muchos gastos | Solo ALIMENTACION, TRANSPORTE, ENTRETENIMIENTO, HOGAR u OTROS; tres triggers impiden crear, editar o borrar (RN-02, RN-03) |
| Gasto | id, usuario_id, categoria_id, monto, fecha, nota, fecha_registro | Registrado | Pertenece a un usuario y a una categoría | Monto > 0 (RN-01); fecha y categoría obligatorias; nota opcional de 200 caracteres (RN-08) |
| Total del mes (calculado) | Suma de montos por usuario, mes y categoría | — | Se calcula desde Gasto | No se guarda, para que coincida con lo registrado (RN-07, RNF-01) |

## Relaciones

USUARIO 1 ──< GASTO >── 1 CATEGORIA

## Cómo crear la base

1. En MySQL Workbench ejecutar db/schema.sql (crea cfp_dev, 3 tablas y 5 categorías).
2. Opcional: ejecutar db/pruebas.sql y luego CALL sp_cfp25_verificar(); (12 pruebas).
3. Arrancar el backend después de crear la base.
4. Para empezar de cero en desarrollo: ejecutar db/reset.sql y repetir el paso 1.

## Notas

- El backend valida que las tablas coincidan con sus entidades: todo cambio se coordina con Backend.
- Las evidencias de las pruebas están en docs/evidencias/CFP-25/.
- Pendiente: teléfono obligatorio (RF-01), a coordinar con Backend.

# Contrato REST de gastos - CFP-24

Responsable: Luis Eduardo García Vázquez (Backend) - Subtarea Jira: **CFP-24**

Trazabilidad: CFP-10, RF-03, RF-04, RF-05, CDU-03, RN-01, RN-02, RN-03, RN-08, CA-01, CA-02, CA-03, CA-09, CA-10, RNF-02, RNF-03, RNF-04.

## Endpoint 1 - POST /api/gastos - Registrar gasto

- Método y ruta: POST /api/gastos
- Entrada (JSON): `monto` (entero en pesos colombianos, mayor que 0), `categoria` (ALIMENTACION | TRANSPORTE | ENTRETENIMIENTO | HOGAR | OTROS), `fecha` (AAAA-MM-DD), `nota` (texto opcional, máx. 200 caracteres). El usuario no va en el cuerpo: se toma de la sesión iniciada.
- Éxito: 201 "Gasto registrado". Devuelve `id`, `monto`, `categoria`, `fecha`, `nota`, `totalMes` y `totalCategoria` ya actualizados (CA-01).
- Error por regla: 400 en monto: "El monto debe ser un número mayor que cero" (RN-01, CA-02). 422 en categoria: "La categoría no es válida. Use una de las cinco predeterminadas" (RN-02, RN-03, CA-09). En ningún rechazo se guarda nada (RNF-02).
- Error por dato: 400 que indica el dato faltante: "Debe seleccionar una categoría" (CA-03) o "La fecha es obligatoria y debe ser válida" (excepción E3 del CDU-03).
- Error por permiso: 401 "Debe iniciar sesión para registrar gastos". Solo un usuario autenticado registra gastos y siempre a su propio nombre (RNF-03).

## Endpoint 2 - GET /api/categorias - Listar las categorías del formulario

- Método y ruta: GET /api/categorias
- Entrada: No recibe datos.
- Éxito: 200 lista con las cinco categorías predeterminadas, cada una con `codigo` y `nombre` (Alimentación, Transporte, Entretenimiento, Hogar, Otros) (RF-04, CA-09).
- Error por regla: No existen POST, PUT ni DELETE sobre categorías: si se intentan, 405 "Las categorías son fijas y no se pueden modificar" (RN-03).
- Error por dato: No aplica: no recibe datos.
- Error por permiso: 401 "Debe iniciar sesión" (RNF-03).

## Endpoint 3 - GET /api/gastos?mes=AAAA-MM - Listar los gastos del mes

- Método y ruta: GET /api/gastos?mes=2026-10
- Entrada: Parámetro `mes` en formato AAAA-MM. Si no se envía, se usa el mes actual. El usuario se toma de la sesión.
- Éxito: 200 lista de los gastos del usuario en ese mes (`id`, `monto`, `categoria`, `fecha`, `nota`) y `totalMes`. El total es exactamente la suma de la lista (CA-01, RN-07, RNF-01). Si no hay gastos: lista vacía y `totalMes` 0.
- Error por regla: Solo se devuelven los gastos del usuario de la sesión, nunca los de otro (RNF-03).
- Error por dato: 400 "El mes debe tener el formato AAAA-MM".
- Error por permiso: 401 "Debe iniciar sesión para consultar sus gastos" (RNF-03).

## Endpoint 4 - GET /api/gastos/{id} - Consultar un gasto registrado

- Método y ruta: GET /api/gastos/{id}
- Entrada: `id` del gasto en la ruta (número). El usuario se toma de la sesión.
- Éxito: 200 el gasto con `id`, `monto`, `categoria`, `fecha`, `nota` y `fecha_registro`. Sirve para comprobar que quedó guardado igual a como se registró (CA-01, RN-07).
- Error por regla: 404 "El gasto no existe".
- Error por dato: 400 "El id del gasto debe ser un número".
- Error por permiso: 401 sin sesión: "Debe iniciar sesión". 403 si el gasto es de otro usuario: "No tiene permiso para ver este gasto" (RNF-03, CA-10).

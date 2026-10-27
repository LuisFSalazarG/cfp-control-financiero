# Matriz de Pruebas - Registrar Gasto (CFP-10)

| Caso | CA | Tipo | Dado / Cuando | Resultado esperado | Evidencia |
|---|---|---|---|---|---|
| **CP-01** | CA-01 | Éxito | Sesión iniciada; monto 50000, ALIMENTACION, 2026-10-16 / se registra | 201; total del mes +$50.000 | Respuesta 201 y consulta BD |
| **CP-02** | CA-02 | Dato faltante | Monto vacío / se registra | 400 "El monto debe ser un número mayor que cero" | Respuesta 400 |
| **CP-03** | CA-02 | Rechazo | Monto 0 o negativo (-5000) / se registra | 400 "El monto debe ser un número mayor que cero" | Respuesta 400 |
| **CP-04** | CA-02 | Rechazo | Monto con letras ("50mil") / se registra | 400 "El monto debe ser un número mayor que cero" | Respuesta 400 |
| **CP-05** | CA-02 | No modificación | Después de los rechazos CP-02 a CP-04 | Cantidad de gastos y total del mes iguales antes y después | Comparación antes/después |
| **CP-06** | CA-03 | Dato faltante | Gasto sin categoría / se registra | 400 "Debe seleccionar una categoría"; no se guarda | Respuesta 400 |
| **CP-07** | CA-09 | Rechazo | Categoría "MASCOTAS", que no existe / se registra | 422 "La categoría no es válida..."; siguen 5 categorías | Respuesta 422 y conteo |
| **CP-08** | CA-01 | Borde | Monto 1 (mínimo válido) / se registra | 201; el gasto se guarda | Respuesta 201 |
| **CP-09** | RF-05 | Dato faltante | Gasto sin fecha / se registra | 400 "La fecha es obligatoria y debe ser válida" | Respuesta 400 |
| **CP-10** | RNF-03 | Permiso | Sin sesión iniciada / se intenta registrar | 401 "Debe iniciar sesión..."; no se guarda | Respuesta 401 |
| **CP-11** | CA-09 | Éxito | Sesión iniciada / se consulta GET /api/categorias | 200 con exactamente las cinco categorías | Respuesta 200 |
| **CP-12** | CA-10 | Permiso | Gasto del usuario A / el usuario B consulta GET /api/gastos/{id} | 403 "No tiene permiso para ver este gasto"; no ve nada | Respuesta 403 |

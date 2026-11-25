# Registro de usuario · CFP-9

`POST /api/usuarios` admite el alta pública. Los demás endpoints de la API requieren autenticación.

```json
{
  "nombreCompleto": "Persona de prueba",
  "correo": "persona@ejemplo.invalid",
  "cedula": "0000000123",
  "telefono": "3000000123",
  "contrasena": "ClaveDeEjemplo"
}
```

Los cinco campos son obligatorios. El nombre admite 150 caracteres, el correo 254 y la cédula y el teléfono 20. La contraseña se almacena con BCrypt y admite hasta 72 bytes UTF-8. Cédula y teléfono son cadenas para conservar los ceros iniciales.

| HTTP | Resultado |
|---|---|
| 201 | Devuelve únicamente el identificador y el correo normalizado en minúsculas. |
| 400 | El cuerpo o algún campo es inválido. El campo `message` explica el rechazo. |
| 409 | El correo o la cédula ya existen. Las restricciones únicas de la tabla protegen las altas simultáneas. |

Después del alta se puede usar HTTP Basic con el correo devuelto y la contraseña elegida.

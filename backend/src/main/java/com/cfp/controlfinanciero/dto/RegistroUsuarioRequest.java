package com.cfp.controlfinanciero.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequest(
        @NotBlank @Size(max = 150) String nombreCompleto,
        @NotBlank @Email @Size(max = 254) String correo,
        @NotBlank @Size(max = 20) String cedula,
        @NotBlank @Size(max = 20) String telefono,
        @NotBlank String contrasena) {
}

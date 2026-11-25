package com.cfp.controlfinanciero.controller;

import com.cfp.controlfinanciero.domain.Usuario;
import com.cfp.controlfinanciero.dto.RegistroUsuarioRequest;
import com.cfp.controlfinanciero.dto.UsuarioRegistrado;
import com.cfp.controlfinanciero.exception.BadRequestException;
import com.cfp.controlfinanciero.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public UsuarioController(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRegistrado registrar(@Valid @RequestBody RegistroUsuarioRequest solicitud) {
        if (solicitud.contrasena().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("La contraseña excede los 72 bytes admitidos");
        }
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(solicitud.nombreCompleto().strip());
        usuario.setCorreo(solicitud.correo().toLowerCase(Locale.ROOT));
        usuario.setCedula(solicitud.cedula().strip());
        usuario.setTelefono(solicitud.telefono().strip());
        usuario.setContrasenaHash(encoder.encode(solicitud.contrasena()));
        usuarios.saveAndFlush(usuario);
        return new UsuarioRegistrado(usuario.getId(), usuario.getCorreo());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> campoInvalido(MethodArgumentNotValidException error) {
        String campo = error.getBindingResult().getFieldErrors().get(0).getField();
        return Map.of("message", "Revise el campo " + campo);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflicto() {
        return Map.of("message", "El correo o la cédula ya están registrados");
    }
}

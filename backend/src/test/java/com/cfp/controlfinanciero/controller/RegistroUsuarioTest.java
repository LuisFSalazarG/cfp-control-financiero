package com.cfp.controlfinanciero.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistroUsuarioTest {
    @Autowired MockMvc mvc;

    private ResultActions registrar(String correo, String cedula) throws Exception {
        String json = """
                {"nombreCompleto":"Prueba Alta","correo":"%s","cedula":"%s",
                 "telefono":"3001234567","contrasena":"ClaveDePrueba9"}
                """.formatted(correo, cedula);
        return mvc.perform(post("/api/usuarios").contentType("application/json").content(json));
    }

    @Test
    void altaPublicaPermiteAutenticarYRechazaDuplicados() throws Exception {
        String correo = "alta@ejemplo.invalid";
        registrar(correo, "cfp9-1").andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.correo").value(correo));
        mvc.perform(get("/api/categorias").with(httpBasic(correo, "ClaveDePrueba9")))
                .andExpect(status().isOk());
        registrar(correo, "cfp9-2").andExpect(status().isConflict());
        registrar("otra@ejemplo.invalid", "cfp9-1").andExpect(status().isConflict());
    }

    @Test
    void rechazaDatosInvalidos() throws Exception {
        registrar("correo-invalido", "cfp9-3").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Revise el campo correo"));
        mvc.perform(post("/api/usuarios").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }
}

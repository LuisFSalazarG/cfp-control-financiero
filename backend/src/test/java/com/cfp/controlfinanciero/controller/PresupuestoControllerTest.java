package com.cfp.controlfinanciero.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PresupuestoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debeCrearPresupuestoYRetornar201() throws Exception {
        String jsonBody = "{\"monto\":1000000}";

        // Asegúrate de que el usuario "a@ejemplo.invalid" con clave "password" exista en tu script de pruebas
        mockMvc.perform(post("/api/presupuestos")
                        .with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());
    }

    @Test
    void debeObtenerAlertasYRetornar200() throws Exception {
        mockMvc.perform(get("/api/presupuestos/alertas")
                        .with(httpBasic("a@ejemplo.invalid", "password")))
                .andExpect(status().isOk());
    }
}
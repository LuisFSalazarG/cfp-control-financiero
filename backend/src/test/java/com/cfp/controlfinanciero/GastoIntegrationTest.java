package com.cfp.controlfinanciero;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GastoIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void cp11_categorias() throws Exception {
        mockMvc.perform(get("/api/categorias").with(httpBasic("a@ejemplo.invalid", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void cp10_sinSesion() throws Exception {
        mockMvc.perform(post("/api/gastos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cp01_registroExitoso() throws Exception {
        String body = "{\"monto\":50000,\"categoria\":\"ALIMENTACION\",\"fecha\":\"2026-10-16\",\"nota\":\"test\"}";
        mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monto").value(50000))
                .andExpect(jsonPath("$.totalMes").value(50000));
    }

    @Test
    void cp02_montoVacio() throws Exception {
        String body = "{\"monto\":null,\"categoria\":\"ALIMENTACION\",\"fecha\":\"2026-10-16\"}";
        mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El monto debe ser un número mayor que cero"));
    }

    @Test
    void cp03_montoCero() throws Exception {
        String body = "{\"monto\":0,\"categoria\":\"ALIMENTACION\",\"fecha\":\"2026-10-16\"}";
        mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El monto debe ser un número mayor que cero"));
    }

    @Test
    void cp04_montoLetras() throws Exception {
        String body = "{\"monto\":\"50mil\",\"categoria\":\"ALIMENTACION\",\"fecha\":\"2026-10-16\"}";
        mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El monto debe ser un número mayor que cero"));
    }

    @Test
    void cp06_sinCategoria() throws Exception {
        String body = "{\"monto\":50000,\"fecha\":\"2026-10-16\"}";
        mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Debe seleccionar una categoría"));
    }

    @Test
    void cp07_categoriaInvalida() throws Exception {
        String body = "{\"monto\":50000,\"categoria\":\"MASCOTAS\",\"fecha\":\"2026-10-16\"}";
        mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("La categoría no es válida. Use una de las cinco predeterminadas"));
    }

    @Test
    void cp09_sinFecha() throws Exception {
        String body = "{\"monto\":50000,\"categoria\":\"ALIMENTACION\"}";
        mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La fecha es obligatoria y debe ser válida"));
    }

    @Test
    void cp08_montoMinimo() throws Exception {
        String body = "{\"monto\":1,\"categoria\":\"ALIMENTACION\",\"fecha\":\"2026-10-16\"}";
        mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void cp12_permiso() throws Exception {
        String body = "{\"monto\":1000,\"categoria\":\"HOGAR\",\"fecha\":\"2026-10-17\"}";
        MvcResult res = mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(get("/api/gastos/" + id).with(httpBasic("b@ejemplo.invalid", "password")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("No tiene permiso para ver este gasto"));
    }

    @Test
    void getPorId() throws Exception {
        String body = "{\"monto\":2000,\"categoria\":\"OTROS\",\"fecha\":\"2026-10-18\"}";
        MvcResult res = mockMvc.perform(post("/api/gastos").with(httpBasic("a@ejemplo.invalid", "password"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();
        String id = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(get("/api/gastos/" + id).with(httpBasic("a@ejemplo.invalid", "password")))
                .andExpect(status().isOk());
    }

    @Test
    void mesInvalido() throws Exception {
        mockMvc.perform(get("/api/gastos?mes=2026-13").with(httpBasic("a@ejemplo.invalid", "password")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El mes debe tener el formato AAAA-MM"));
    }

    @Test
    void idInvalido() throws Exception {
        mockMvc.perform(get("/api/gastos/abc").with(httpBasic("a@ejemplo.invalid", "password")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El id del gasto debe ser un número"));
    }

    @Test
    void noExiste() throws Exception {
        mockMvc.perform(get("/api/gastos/999999").with(httpBasic("a@ejemplo.invalid", "password")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("El gasto no existe"));
    }

    @Test
    void categoriasMetodoNoPermitido() throws Exception {
        mockMvc.perform(post("/api/categorias").with(httpBasic("a@ejemplo.invalid", "password")))
                .andExpect(status().isMethodNotAllowed());
    }
}

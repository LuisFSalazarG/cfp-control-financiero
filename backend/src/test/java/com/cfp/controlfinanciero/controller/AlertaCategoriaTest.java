package com.cfp.controlfinanciero.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlertaCategoriaTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate db;

    private ResultActions gastar(String usuario, String categoria, int monto, String fecha) throws Exception {
        String json = """
                {"monto":%d,"categoria":"%s","fecha":"%s"}
                """.formatted(monto, categoria, fecha);
        return mvc.perform(post("/api/gastos").with(httpBasic(usuario + "@ejemplo.invalid", "password"))
                        .contentType("application/json").content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void alertaUnaVezPorUsuarioCategoriaYMes() throws Exception {
        for (int mes : new int[]{5, 6}) {
            db.update("""
                    INSERT INTO presupuesto_categoria
                      (usuario_id,categoria_id,anio,mes,monto_limite,alerta_70_emitida)
                    SELECT u.id,c.id,2035,?,200000,false FROM usuario u CROSS JOIN categoria c
                     WHERE u.correo='a@ejemplo.invalid' AND c.codigo='ENTRETENIMIENTO'
                    """, mes);
        }
        String categoria = "ENTRETENIMIENTO";
        gastar("a", categoria, 120000, "2035-05-01").andExpect(jsonPath("$.alertaCategoria").doesNotExist());
        gastar("b", categoria, 150000, "2035-05-01").andExpect(jsonPath("$.alertaCategoria").doesNotExist());
        gastar("a", "HOGAR", 150000, "2035-05-01").andExpect(jsonPath("$.alertaCategoria").doesNotExist());
        gastar("a", categoria, 20000, "2035-05-02")
                .andExpect(jsonPath("$.alertaCategoria.nivel").value(70))
                .andExpect(jsonPath("$.alertaCategoria.categoria").value(categoria))
                .andExpect(jsonPath("$.alertaCategoria.sugerencia").isNotEmpty());
        gastar("a", categoria, 500, "2035-05-03").andExpect(jsonPath("$.alertaCategoria").doesNotExist());
        gastar("a", categoria, 145000, "2035-06-01").andExpect(jsonPath("$.alertaCategoria.nivel").value(70));
    }
}

package com.cfp.controlfinanciero.config;

import com.cfp.controlfinanciero.domain.Categoria;
import com.cfp.controlfinanciero.domain.Usuario;
import com.cfp.controlfinanciero.repository.CategoriaRepository;
import com.cfp.controlfinanciero.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initData(CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (categoriaRepository.count() == 0) {
                List.of(
                    new String[]{"ALIMENTACION", "Alimentación"},
                    new String[]{"TRANSPORTE", "Transporte"},
                    new String[]{"ENTRETENIMIENTO", "Entretenimiento"},
                    new String[]{"HOGAR", "Hogar"},
                    new String[]{"OTROS", "Otros"}
                ).forEach(pair -> {
                    Categoria c = new Categoria();
                    c.setCodigo(pair[0]);
                    c.setNombre(pair[1]);
                    categoriaRepository.save(c);
                });
            }
            if (usuarioRepository.findByCorreo("a@ejemplo.invalid").isEmpty()) {
                Usuario u = new Usuario();
                u.setNombreCompleto("Usuario A");
                u.setCorreo("a@ejemplo.invalid");
                u.setCedula("0000000001");
                u.setTelefono(null);
                u.setContrasenaHash(passwordEncoder.encode("password"));
                usuarioRepository.save(u);
            }
            if (usuarioRepository.findByCorreo("b@ejemplo.invalid").isEmpty()) {
                Usuario u = new Usuario();
                u.setNombreCompleto("Usuario B");
                u.setCorreo("b@ejemplo.invalid");
                u.setCedula("0000000002");
                u.setTelefono(null);
                u.setContrasenaHash(passwordEncoder.encode("password"));
                usuarioRepository.save(u);
            }
        };
    }
}

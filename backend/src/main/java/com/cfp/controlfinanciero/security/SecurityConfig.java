package com.cfp.controlfinanciero.security;

import com.cfp.controlfinanciero.domain.Usuario;
import com.cfp.controlfinanciero.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> {
            Usuario u = usuarioRepository.findByCorreo(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return User.withUsername(u.getCorreo())
                    .password(u.getContrasenaHash())
                    .roles("USER")
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> {})
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setHeader("WWW-Authenticate", "Basic realm=\"cfp\"");
                    escribirError(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Debe iniciar sesión con credenciales válidas para usar el servicio");
                })
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    escribirError(response, HttpServletResponse.SC_FORBIDDEN,
                            "No tiene permiso para realizar esta acción"))
            );
        return http.build();
    }

    private static void escribirError(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"message\":\"" + mensaje + "\"}");
    }
}

package com.unicid.ga.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Desativa CSRF para facilitar testes mobile
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Libera Login e Registro
                        .requestMatchers("/scanner/**").permitAll() // Libera o scanner temporariamente
                        .requestMatchers("/eventos.html").permitAll() // Libera a página HTML de eventos
                        .requestMatchers("/api/eventos/**").permitAll() // Libera a API de eventos para a página funcionar
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
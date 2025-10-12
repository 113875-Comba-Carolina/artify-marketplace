package com.carolinacomba.marketplace.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos para registro y autenticación
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/registro/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                
                // Endpoints públicos de productos (lectura)
                .requestMatchers("GET", "/api/productos/**").permitAll()
                
                // Webhook de Mercado Pago (debe ser público)
                .requestMatchers("POST", "/api/payments/webhook").permitAll()
                
                // Endpoints de consulta de pagos (públicos para debugging)
                .requestMatchers("GET", "/api/payments/status-by-reference/**").permitAll()
                .requestMatchers("GET", "/api/payments/debug/**").permitAll()
                
                // Endpoints para ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Todos los demás requieren autenticación
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.realmName("Artify Marketplace"));

        return http.build();
    }
}
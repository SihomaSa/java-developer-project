package com.empresa.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security
 * - Stateless (REST API)
 * - Autorización por roles
 * - BCrypt para passwords
 * - Permite Swagger UI públicamente
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI público
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Actuator health público
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // Actuator completo solo ADMIN
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // GET público
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                        // Escritura requiere autenticación
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {}) // Basic Auth para demo (usar JWT en producción)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // En producción: usar base de datos con UserDetailsService real
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password(encoder.encode("admin123"))
                        .roles("ADMIN", "PRODUCTOS", "INVENTARIO")
                        .build(),
                User.withUsername("inventario")
                        .password(encoder.encode("inv123"))
                        .roles("INVENTARIO")
                        .build(),
                User.withUsername("usuario")
                        .password(encoder.encode("usr123"))
                        .roles("USER")
                        .build()
        );
    }
}

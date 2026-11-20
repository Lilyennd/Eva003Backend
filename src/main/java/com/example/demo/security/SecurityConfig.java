package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        http
            .csrf(c -> c.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/error", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                // Publico
                .requestMatchers(HttpMethod.POST, "/login", "/login/**", "/usuarios", "/contacto").permitAll()
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/productos/criticos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/productos", "/productos/*", "/productos/ofertas",
                        "/categorias", "/categorias/*", "/blogs", "/blogs/*", "/regiones", "/comunas").permitAll()
                .requestMatchers(HttpMethod.POST, "/imagenes/*").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.POST, "/blogs").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.PUT, "/blogs/*").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(HttpMethod.GET, "/ordenes", "/ordenes/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/ordenes").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/ordenes/*").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/usuarios/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/usuarios/*").authenticated()
                .anyRequest().hasRole("ADMIN"))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(401);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"error\":\"No autenticado\"}");
                })
                .accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(403);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"error\":\"No tienes permisos para esta acción\"}");
                }))
            .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOriginPatterns(List.of("http://localhost:*"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}
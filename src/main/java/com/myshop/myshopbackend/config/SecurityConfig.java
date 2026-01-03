package com.myshop.myshopbackend.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Isse hum Render ka URL environment variable se uthayenge
    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", 
                    "/api/users/register", 
                    "/api/users/login", 
                    "/api/users/loginSuccess", 
                    "/api/users/forgot-password",
                    "/api/users/reset-password",
                    "/api/users/update-role",
                    "/api/shops/**", 
                    "/api/products/**", 
                    "/api/orders/**", 
                    "/api/chat/**", 
                    "/api/staff/**",
                    "/uploads/**",
                    "/login/**", 
                    "/oauth2/**",
                    "/api/payment/**"
                ).permitAll() 
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                // Render ke URL par redirect karega
                .defaultSuccessUrl(backendUrl + "/api/users/loginSuccess", true)
            )
            .logout(logout -> logout
                // Logout ke baad local frontend par bhejega
                .logoutSuccessUrl("http://127.0.0.1:5500/frontend/login.html")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            backendUrl,              // Dynamic Render Backend URL
            "http://127.0.0.1:5500", 
            "http://localhost:5500",
            "http://localhost:5173"  // Vite/React ke liye
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
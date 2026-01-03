package com.myshop.myshopbackend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.backend.url:https://myshop-backend-final-1.onrender.com}")
    private String backendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/", 
                    "/api/users/**", 
                    "/api/shops/**", 
                    "/api/products/**", 
                    "/api/orders/**", 
                    "/api/chat/**", 
                    "/api/staff/**",
                    "/uploads/**",
                    "/login/**", 
                    "/oauth2/**",
                    "/api/payment/**",
                    "/error" // Isse allow karna zaroori hai taaki error message dikhe
                ).permitAll() 
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl(backendUrl + "/api/users/loginSuccess", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("https://myshop-backend-final.vercel.app/index.html")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Sabhi origins allow kar rahe hain testing ke liye
        config.setAllowedOriginPatterns(List.of("*")); 
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
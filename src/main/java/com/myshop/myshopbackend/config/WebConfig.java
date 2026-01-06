package com.myshop.myshopbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Render ka URL (Default set to your Render live link)
    @Value("${app.backend.url:https://myshop-backend-final-1.onrender.com}")
    private String backendUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Yeh line batati hai ki server par /uploads/ ka matlab 'uploads' folder hai
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Yeh setting frontend ko backend se connect karne ki permission deti hai
        registry.addMapping("/**")
                .allowedOrigins(
                    backendUrl,                                // Render Backend URL
                    "https://myshop-pro-tushar-2004.vercel.app", // Aapka Vercel Frontend URL
                    "http://localhost:5500", 
                    "http://127.0.0.1:5500",
                    "http://localhost:5173"                    // React/Vite ke liye
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
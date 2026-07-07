package com.aryan.omybott.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration publicCors = new CorsConfiguration();
        publicCors.setAllowedOriginPatterns(List.of("*"));
        publicCors.setAllowedMethods(List.of("*"));
        publicCors.setAllowedHeaders(List.of("*"));
        publicCors.setAllowCredentials(false);

        CorsConfiguration appCors = new CorsConfiguration();
        appCors.setAllowedOriginPatterns(allowedOrigins);
        appCors.setAllowedMethods(List.of("*"));
        appCors.setAllowedHeaders(List.of("*"));
        appCors.setAllowCredentials(true);
        appCors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/public/**", publicCors);
        source.registerCorsConfiguration("/**", appCors);

        return source;
    }

}

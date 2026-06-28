package com.aryan.omybott.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration publicCors = new CorsConfiguration();
        publicCors.setAllowedOriginPatterns(List.of("*"));
        publicCors.setAllowedMethods(List.of("*"));
        publicCors.setAllowedHeaders(List.of("*"));
        publicCors.setAllowCredentials(true);

        CorsConfiguration defaultCors = new CorsConfiguration();
        defaultCors.setAllowedOrigins(List.of("http://localhost:3000"));
        defaultCors.setAllowedMethods(List.of("*"));
        defaultCors.setAllowedHeaders(List.of("*"));
        defaultCors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/public/**", publicCors);
        source.registerCorsConfiguration("/**", defaultCors);

        return source;
    }

}

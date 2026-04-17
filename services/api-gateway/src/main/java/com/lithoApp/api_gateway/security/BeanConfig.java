package com.lithoApp.api_gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        //Allow cookies/credentials like JWT in Authorization header
        config.setAllowCredentials(true);
        // Only allow Angular dev server (localhost:4200)
        config.setAllowedOrigins(List.of(
                "http://localhost:4200"
        ));
        // Allow specific headers that Angular might send
//        HttpHeaders.ACCEPT,HttpHeaders.AUTHORIZATION,HttpHeaders.CONTENT_TYPE,HttpHeaders.ORIGIN
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.ACCEPT,
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ORIGIN,
                HttpHeaders.HOST,
                "X-Requested-With",
                "X-Forwarded-For",
                "X-Forwarded-Host",
                "X-Forwarded-Proto"
        ));
        // Allow frontend-backend communication through proxy/ngrok
        config.setExposedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_DISPOSITION
        ));
        //Allow HTTP methods your API supports "GET", "POST", "PUT", "DELETE", "PATCH"
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        //Apply this config to all endpoints
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

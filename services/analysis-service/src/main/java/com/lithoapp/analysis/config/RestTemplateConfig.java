package com.lithoapp.analysis.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Produces a shared {@link RestTemplate} bean used by inter-service HTTP clients
 * (e.g. {@link com.lithoapp.analysis.client.HttpPatientServiceClient}).
 *
 * <p>{@code RestTemplateBuilder.connectTimeout(Duration)} and {@code readTimeout(Duration)}
 * were removed in Spring Boot 3.2. {@code builder.build()} is the minimal compatible form.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

package com.lithoapp.drainage.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Produces a shared {@link RestTemplate} bean used by inter-service HTTP clients
 * (e.g. {@link com.lithoapp.drainage.client.HttpPatientServiceClient}).
 *
 * <p>Timeout customisation via {@code RestTemplateBuilder.connectTimeout(Duration)} /
 * {@code readTimeout(Duration)} was removed in Spring Boot 3.2 — those fluent methods
 * no longer exist on the builder. Timeouts must now be set on the underlying
 * {@link org.springframework.http.client.ClientHttpRequestFactory} directly.
 * Using {@code builder.build()} is the minimal compatible form and sufficient for
 * internal service-to-service calls in a controlled network.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

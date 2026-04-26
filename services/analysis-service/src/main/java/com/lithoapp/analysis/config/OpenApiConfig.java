package com.lithoapp.analysis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI analysisServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LithoApp — Analysis Service API")
                        .description("""
                                Manages biological and stone analysis requests for the LithoApp platform.
                                Handles the biologist workflow: metabolic PDF uploads (blood, 24h urine, spot urine),
                                structured stone-analysis results, request lifecycle (CREATED → IN_PROGRESS → COMPLETED),
                                and a full audit trail of all mutations.
                                Every analysis request is anchored to an episode, which belongs to a patient.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LithoApp Team")
                                .email("dev@lithoapp.com")));
    }
}

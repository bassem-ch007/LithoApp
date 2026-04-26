package com.lithoapp.drainage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI drainageServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LithoApp — Drainage Service API")
                        .description("""
                                Manages urinary drainage devices (JJ stents, nephrostomies, catheters) placed during
                                the clinical episode of a kidney stone patient. Tracks placement, planned removal,
                                overdue state, and removal events. Every drainage is anchored to an episode.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LithoApp Team")
                                .email("dev@lithoapp.com")));
    }
}

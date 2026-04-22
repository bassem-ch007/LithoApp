package com.lithoapp.episodeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI episodeServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LithoApp — Episode Service API")
                        .description("""
                                Manages clinical episodes for urological kidney stone patients.
                                An episode is the top-level aggregate grouping analysis requests
                                and drainage procedures for a single stone-forming event.
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("LithoApp Team")));
    }
}

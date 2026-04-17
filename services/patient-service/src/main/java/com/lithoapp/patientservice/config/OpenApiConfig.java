package com.lithoapp.patientservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lithoAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LithoApp – Patient Service API")
                        .description("Manages patient identity, demographics, and structured clinical data for the LithoApp clinical workflow platform.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LithoApp Team")
                                .email("dev@lithoapp.com")));
    }
}

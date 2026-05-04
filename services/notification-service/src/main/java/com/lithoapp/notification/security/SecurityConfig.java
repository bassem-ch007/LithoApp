package com.lithoapp.notification.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakJwtAuthConverter keycloakJwtAuthConverter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
                // Internal event ingestion: producers call this in-cluster, often with a JWT
                // forwarded from the originating user but sometimes anonymously (scheduler).
                // External traffic to this path must reach the gateway first, which enforces JWT.
                // TODO(security): replace the blanket permitAll with a dedicated service-account
                //   client_credentials role (e.g. ROLE_NOTIFICATION_PRODUCER) once Keycloak service
                //   accounts are provisioned for analysis-service / drainage-service / scheduler.
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/notifications/events").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(o -> o.jwt(j ->
                j.jwtAuthenticationConverter(keycloakJwtAuthConverter)));
        return http.build();
    }
}

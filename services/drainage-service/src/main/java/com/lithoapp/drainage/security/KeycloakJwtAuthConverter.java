package com.lithoapp.drainage.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KeycloakJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> defaultAuthorities = defaultConverter.convert(jwt);
        Collection<GrantedAuthority> realmAuthorities = extractRealmRoles(jwt);

        Collection<GrantedAuthority> combined = Stream.concat(
                defaultAuthorities != null ? defaultAuthorities.stream() : Stream.empty(),
                realmAuthorities.stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, combined,
                jwt.getClaimAsString("preferred_username"));
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        // Primary source: realm_access.roles (Keycloak realm roles)
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Stream<String> realmRoles = Stream.empty();
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            realmRoles = ((List<String>) realmAccess.get("roles")).stream();
        }

        // Fallback source: resource_access.<any-client>.roles (Keycloak client roles)
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        Stream<String> clientRoles = Stream.empty();
        if (resourceAccess != null) {
            clientRoles = resourceAccess.values().stream()
                    .filter(v -> v instanceof Map)
                    .map(v -> (Map<String, Object>) v)
                    .filter(m -> m.containsKey("roles"))
                    .flatMap(m -> ((List<String>) m.get("roles")).stream());
        }

        return Stream.concat(realmRoles, clientRoles)
                .filter(r -> r.equals("ADMIN") || r.equals("UROLOGUE") || r.equals("BIOLOGIST"))
                .distinct()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
    }
}

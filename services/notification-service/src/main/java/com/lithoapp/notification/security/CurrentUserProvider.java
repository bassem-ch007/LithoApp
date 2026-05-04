package com.lithoapp.notification.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CurrentUserProvider {

    public Jwt getJwt() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new IllegalStateException("No JWT authentication in security context");
    }

    public String getUsername() {
        return getJwt().getClaimAsString("preferred_username");
    }

    public String getSubject() {
        return getJwt().getSubject();
    }

    public String getEmail() {
        return getJwt().getClaimAsString("email");
    }

    /** App roles owned by the caller (subset of ADMIN, UROLOGUE, BIOLOGIST). */
    public Set<String> getAppRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Set.of();
        }
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .filter(r -> List.of("ADMIN", "UROLOGUE", "BIOLOGIST").contains(r))
                .collect(java.util.stream.Collectors.toSet());
        return roles;
    }

    public boolean isAdmin() {
        return getAppRoles().contains("ADMIN");
    }
}

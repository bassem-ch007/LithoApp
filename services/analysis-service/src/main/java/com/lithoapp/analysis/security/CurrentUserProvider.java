package com.lithoapp.analysis.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Jwt getJwt() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new IllegalStateException("No JWT authentication in security context");
    }

    /** Returns the Keycloak preferred_username (e.g. "dr.smith"). */
    public String getUsername() {
        return getJwt().getClaimAsString("preferred_username");
    }

    /** Returns the Keycloak subject UUID (stable identifier). */
    public String getSubject() {
        return getJwt().getSubject();
    }
}

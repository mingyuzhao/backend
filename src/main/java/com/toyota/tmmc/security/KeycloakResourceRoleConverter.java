package com.toyota.tmmc.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KeycloakResourceRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final String requiredClientId;

    public KeycloakResourceRoleConverter(String requiredClientId) {
        this.requiredClientId = requiredClientId;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object resourceAccess = jwt.getClaim("resource_access");
        if (!(resourceAccess instanceof Map<?, ?> resourceAccessMap)) {
            return Collections.emptySet();
        }

        Object clientAccess = resourceAccessMap.get(requiredClientId);
        if (!(clientAccess instanceof Map<?, ?> clientAccessMap)) {
            return Collections.emptySet();
        }

        Object roles = clientAccessMap.get("roles");
        if (!(roles instanceof Collection<?> roleCollection)) {
            return Collections.emptySet();
        }

        return roleCollection.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(role -> !role.isBlank())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase(Locale.ROOT)))
                .collect(Collectors.toSet());
    }
}




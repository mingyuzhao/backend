package com.toyota.tmmc.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;


public class KeycloakAudienceValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error ERROR =
            new OAuth2Error("invalid_token", "Required audience/client mapping is missing", null);

    private final String requiredClientId;

    public KeycloakAudienceValidator(String requiredClientId) {
        this.requiredClientId = requiredClientId;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        boolean audMatch = jwt.getAudience().contains(requiredClientId);
        String authorizedParty = jwt.getClaimAsString("azp");
        boolean azpMatch = requiredClientId.equals(authorizedParty);

        if (audMatch || azpMatch) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(ERROR);
    }
}



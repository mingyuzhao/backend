package com.toyota.tmmc.security;

import com.toyota.tmmc.config.JwtProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final byte[] key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(String username, String role) {
        try {
            Date issuedAt = new Date();
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .claim("role", role)
                    .issueTime(issuedAt)
                    .expirationTime(new Date(issuedAt.getTime() + jwtProperties.expirationMs()))
                    .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(new MACSigner(key));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to generate JWT", e);
        }
    }

    public String extractUsername(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public JWTClaimsSet getClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(new MACVerifier(key))) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }
            return signedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public String extractRole(String token) {
        try {
            return getClaims(token).getStringClaim("role");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            JWTClaimsSet claims = getClaims(token);
            return claims.getSubject() != null
                    && claims.getExpirationTime() != null
                    && claims.getExpirationTime().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token, String expectedUsername) {
        try {
            JWTClaimsSet claims = getClaims(token);
            return expectedUsername.equals(claims.getSubject())
                    && claims.getExpirationTime() != null
                    && claims.getExpirationTime().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}


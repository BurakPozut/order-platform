package com.burakpozut.auth_service.infra.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenHashService {
    private final String saltSecret;

    public TokenHashService(@Value("${jwt.secret}") String jwtSecret) {
        this.saltSecret = jwtSecret;
    }

    public String hash(String token) {
        try {
            // We switched from bcrypt from SHA-256. This is a token not a password so this
            // is fine and faster for faster look up.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            String saltedToken = token + saltSecret;
            byte[] hashBytes = digest.digest(saltedToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);

        }
    }

    public boolean matches(String token, String hash) {
        String tokenHash = hash(token);
        return tokenHash.equals(hash);
    }

}

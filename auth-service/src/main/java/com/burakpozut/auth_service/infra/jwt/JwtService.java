package com.burakpozut.auth_service.infra.jwt;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.f4b6a3.uuid.UuidCreator;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final String issuer;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private static final SecureRandom SecureRandom = new SecureRandom();
    private static final Base64.Encoder Base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(accessTokenExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        SecureRandom.nextBytes(randomBytes);

        String tokenId = UuidCreator.getTimeOrdered().toString();
        String secret = Base64UrlEncoder.encodeToString(randomBytes);
        String refreshToken = tokenId + "." + secret;

        return refreshToken;
    }

    public String extractTokenId(String refreshToken) {
        int dotIndex = refreshToken.indexOf(".");
        if (dotIndex == -1) {
            throw new IllegalArgumentException("Invalid refresh token format");
        }

        return refreshToken.substring(0, dotIndex);
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
package com.burakpozut.auth_service.domain;

import java.time.Instant;
import java.util.UUID;

import com.burakpozut.common.exception.DomainValidationException;

public record RefreshToken(
        UUID id,
        UUID userId,
        String tokenHash,
        Instant expiresAt,
        Instant revokedAt) {

    public RefreshToken {
        if (id == null)
            throw new DomainValidationException("Id cannot be null");
        if (userId == null)
            throw new DomainValidationException("User id cannot be null");
        if (tokenHash == null || tokenHash.isBlank())
            throw new DomainValidationException("Token hash cannot be null or blank");
        if (expiresAt == null)
            throw new DomainValidationException("Expires at cannot be null");
    }

    public static RefreshToken createNew(UUID userId, String tokenHash, Instant expiresAt) {
        UUID id = UUID.randomUUID();
        return new RefreshToken(id, userId, tokenHash, expiresAt, null);
    }

    public static RefreshToken rehydrate(UUID id, UUID userId, String tokenHash, Instant expiresAt, Instant revokedAt) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revokedAt);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isValid() {
        return !isExpired() && !isRevoked();
    }
}
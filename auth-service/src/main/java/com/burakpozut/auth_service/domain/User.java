package com.burakpozut.auth_service.domain;

import java.util.UUID;

import com.burakpozut.common.exception.DomainValidationException;

public record User(
        UUID id,
        String email,
        String passwordHash,
        boolean enabled) {

    public User {
        if (id == null)
            throw new DomainValidationException("Id cannot be null");
        if (email == null || email.isBlank())
            throw new DomainValidationException("Email cannot be null or blank");
        if (passwordHash == null || passwordHash.isBlank())
            throw new DomainValidationException("Password hash cannot be null or blank");
    }

    public static User createNew(String email, String passwordHash) {
        UUID id = UUID.randomUUID();
        return new User(id, email, passwordHash, true);
    }

    public static User rehydrate(UUID id, String email, String passwordHash, boolean enabled) {
        return new User(id, email, passwordHash, enabled);
    }
}
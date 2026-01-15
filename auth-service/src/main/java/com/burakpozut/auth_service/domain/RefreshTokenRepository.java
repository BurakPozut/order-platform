package com.burakpozut.auth_service.domain;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    Optional<RefreshToken> findByTokenId(String tokenId);

    RefreshToken save(RefreshToken token, boolean isNew);

    void deleteByUserId(UUID userId);
}

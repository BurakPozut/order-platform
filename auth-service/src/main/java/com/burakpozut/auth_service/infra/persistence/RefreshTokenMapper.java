package com.burakpozut.auth_service.infra.persistence;

import com.burakpozut.auth_service.domain.RefreshToken;

public class RefreshTokenMapper {
    static RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.rehydrate(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getRevokedAt());
    }

    static RefreshTokenJpaEntity toEntity(RefreshToken token, boolean isNew) {
        var entity = new RefreshTokenJpaEntity();
        entity.setId(token.id());
        entity.setUserId(token.userId());
        entity.setTokenId(token.tokenId());
        entity.setTokenHash(token.tokenHash());
        entity.setExpiresAt(token.expiresAt());
        entity.setRevokedAt(token.revokedAt());
        entity.setNew(isNew);
        return entity;
    }
}
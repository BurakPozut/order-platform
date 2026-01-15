package com.burakpozut.auth_service.infra.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.auth_service.domain.RefreshToken;
import com.burakpozut.auth_service.domain.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {
    private final SpringDataRefreshTokenRepository jpa;

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpa.findByTokenHash(tokenHash).map(RefreshTokenMapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken token, boolean isNew) {
        var entity = RefreshTokenMapper.toEntity(token, isNew);
        var savedEntity = jpa.save(entity);
        return RefreshTokenMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpa.deleteByUserId(userId);
    }
}
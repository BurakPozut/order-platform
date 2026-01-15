package com.burakpozut.auth_service.infra.persistence;

import com.burakpozut.auth_service.domain.User;

public class UserMapper {
    static User toDomain(UserJpaEntity entity) {
        return User.rehydrate(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getEnabled());
    }

    static UserJpaEntity toEntity(User user, boolean isNew) {
        var entity = new UserJpaEntity();
        entity.setId(user.id());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setEnabled(user.enabled());
        entity.setNew(isNew);
        return entity;
    }
}
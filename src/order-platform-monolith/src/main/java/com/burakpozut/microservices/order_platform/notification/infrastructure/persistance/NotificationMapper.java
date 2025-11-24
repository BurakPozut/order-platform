package com.burakpozut.microservices.order_platform.notification.infrastructure.persistance;

import com.burakpozut.microservices.order_platform.notification.domain.Notification;

public class NotificationMapper {
  public static Notification toDomain(NotificationJpaEntity entity) {
    return Notification.rehydrate(
        entity.getId(),
        entity.getCustomerId(),
        entity.getOrderId(),
        entity.getType(),
        entity.getChanned(),
        entity.getStatus());
  }

  public static NotificationJpaEntity toEntity(Notification n, boolean isNew) {
    var entity = new NotificationJpaEntity();
    entity.setId(n.getId());
    entity.setCustomerId(n.getCustomerId());
    entity.setOrderId(n.getOrderId());
    entity.setType(n.getType());
    entity.setChanned(n.getChanned());
    entity.setStatus(n.getStatus());
    entity.setNew(isNew);
    return entity;
  }

}

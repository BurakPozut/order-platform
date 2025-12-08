package com.burakpozut.notification_service.infra.persistance;

import com.burakpozut.notification_service.domain.Notification;

public class NotificationMapper {
  public static Notification toDomain(NotificationJpaEntity entity) {
    return Notification.rehydrate(
        entity.getId(),
        entity.getCustomerId(),
        entity.getOrderId(),
        entity.getType(),
        entity.getChannel(),
        entity.getStatus());
  }

  public static NotificationJpaEntity toEntity(Notification notification, boolean isNew) {
    NotificationJpaEntity entity = new NotificationJpaEntity();
    entity.setId(notification.id());
    entity.setCustomerId(notification.customerId());
    entity.setOrderId(notification.orderId());
    entity.setType(notification.type());
    entity.setChannel(notification.channel());
    entity.setStatus(notification.status());
    entity.setNew(isNew);
    return entity;
  }
}
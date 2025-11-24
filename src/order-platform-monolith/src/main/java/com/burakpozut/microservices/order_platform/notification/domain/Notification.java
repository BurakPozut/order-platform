package com.burakpozut.microservices.order_platform.notification.domain;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.exception.DomainValidationException;

import lombok.Getter;

@Getter
public class Notification {

  private final UUID id;
  private final UUID customerId;
  private final UUID orderId;
  private final NotificationType type;
  private final String channed;
  private final NotificationStatus status;

  private Notification(UUID id, UUID customerId, UUID orderId, NotificationType type, String channed,
      NotificationStatus status) {
    if (id == null)
      throw new DomainValidationException("id cannot be null");
    if (customerId == null)
      throw new DomainValidationException("Customer id cannot be null");
    if (type == null)
      throw new DomainValidationException("Type cannot be null");
    if (channed == null)
      throw new DomainValidationException("Channed cannot be null");
    if (status == null)
      throw new DomainValidationException("Status cannot be null");

    this.id = id;
    this.customerId = customerId;
    this.orderId = orderId;
    this.type = type;
    this.channed = channed;
    this.status = status;
  }

  public static Notification createNew(UUID customerId, UUID orderId, NotificationType type, String channed,
      NotificationStatus status) {
    UUID id = UUID.randomUUID();
    return new Notification(id, customerId, orderId, type, channed, status);
  }

  public static Notification rehydrate(UUID id, UUID customerId, UUID orderId, NotificationType type, String channed,
      NotificationStatus status) {
    return new Notification(id, customerId, orderId, type, channed, status);
  }
}

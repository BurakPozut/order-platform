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
  private final String channel;
  private final NotificationStatus status;

  private Notification(UUID id, UUID customerId, UUID orderId, NotificationType type, String channel,
      NotificationStatus status) {
    if (id == null)
      throw new DomainValidationException("id cannot be null");
    if (customerId == null)
      throw new DomainValidationException("Customer id cannot be null");
    if (type == null)
      throw new DomainValidationException("Type cannot be null");
    if (channel == null)
      throw new DomainValidationException("Channel cannot be null");
    if (status == null)
      throw new DomainValidationException("Status cannot be null");

    this.id = id;
    this.customerId = customerId;
    this.orderId = orderId;
    this.type = type;
    this.channel = channel;
    this.status = status;
  }

  public static Notification createNew(UUID customerId, UUID orderId, NotificationType type, String channel,
      NotificationStatus status) {
    UUID id = UUID.randomUUID();
    return new Notification(id, customerId, orderId, type, channel, status);
  }

  public static Notification rehydrate(UUID id, UUID customerId, UUID orderId, NotificationType type, String channel,
      NotificationStatus status) {
    return new Notification(id, customerId, orderId, type, channel, status);
  }
}

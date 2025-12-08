package com.burakpozut.notification_service.domain;

import java.util.UUID;

import com.burakpozut.common.exception.DomainValidationException;

public record Notification(
    UUID id,
    UUID customerId,
    UUID orderId,
    NotificationType type,
    NotificationChannel channel,
    NotificationStatus status) {
  public Notification {
    if (id == null)
      throw new DomainValidationException("Notification id cannot be null");
    if (customerId == null)
      throw new DomainValidationException("Customer id cannot be null");
    if (orderId == null)
      throw new DomainValidationException("Order id cannot be null");
    if (type == null)
      throw new DomainValidationException("Type cannot be null or blank");
    if (channel == null)
      throw new DomainValidationException("Channel cannot be null or blank");
    if (status == null)
      throw new DomainValidationException("Status cannot be null or blank");
  }

  public static Notification of(
      UUID customerId,
      UUID orderId,
      NotificationType type,
      NotificationChannel channel,
      NotificationStatus status) {
    return new Notification(
        UUID.randomUUID(),
        customerId,
        orderId,
        type,
        channel,
        status);
  }

  public static Notification rehydrate(
      UUID id,
      UUID customerId,
      UUID orderId,
      NotificationType type,
      NotificationChannel channel,
      NotificationStatus status) {
    return new Notification(
        id,
        customerId,
        orderId,
        type,
        channel,
        status);
  }

}

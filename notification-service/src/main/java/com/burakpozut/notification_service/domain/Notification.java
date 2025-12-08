package com.burakpozut.notification_service.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.burakpozut.common.exception.DomainValidationException;

public record Notification(
    UUID id,
    UUID customerId,
    UUID orderId,
    String type,
    String channel,
    String status,
    LocalDateTime createdAt) {
  public Notification {
    if (id == null)
      throw new DomainValidationException("Notification id cannot be null");
    if (customerId == null)
      throw new DomainValidationException("Customer id cannot be null");
    if (orderId == null)
      throw new DomainValidationException("Order id cannot be null");
    if (type == null || type.isBlank())
      throw new DomainValidationException("Type cannot be null or blank");
    if (channel == null || channel.isBlank())
      throw new DomainValidationException("Channel cannot be null or blank");
    if (status == null || status.isBlank())
      throw new DomainValidationException("Status cannot be null or blank");
    if (createdAt == null)
      throw new DomainValidationException("CreatedAt cannot be null");
  }

  public static Notification of(
      UUID customerId,
      UUID orderId,
      String type,
      String channel,
      String status) {
    return new Notification(
        UUID.randomUUID(),
        customerId,
        orderId,
        type,
        channel,
        status,
        java.time.LocalDateTime.now());
  }

  public static Notification rehydrate(
      UUID id,
      UUID customerId,
      UUID orderId,
      String type,
      String channel,
      String status,
      java.time.LocalDateTime createdAt) {
    return new Notification(
        id,
        customerId,
        orderId,
        type,
        channel,
        status,
        createdAt);
  }

}

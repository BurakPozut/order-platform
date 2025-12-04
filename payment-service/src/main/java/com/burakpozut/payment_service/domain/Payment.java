package com.burakpozut.payment_service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Payment(
    UUID id,
    UUID orderId,
    BigDecimal amount,
    String currency,
    String status,
    String provider,
    String providerRef,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public Payment {
    if (id == null)
      throw new IllegalArgumentException("Payment Id cannot be null");
    if (orderId == null)
      throw new IllegalArgumentException("Order Id cannot be null");
    if (amount == null)
      throw new IllegalArgumentException("Amount cannot be null");
    if (amount.compareTo(BigDecimal.ZERO) < 0)
      throw new IllegalArgumentException("Amount cannot be negative");
    if (currency == null || currency.isBlank())
      throw new IllegalArgumentException("Currency cannot be null or blank");
    if (status == null || status.isBlank())
      throw new IllegalArgumentException("Status cannot be null or blank");
    if (provider == null || provider.isBlank())
      throw new IllegalArgumentException("Provider cannot be null or blank");
  }

  public static Payment of(
      UUID orderId,
      BigDecimal amount,
      String currency,
      String status,
      String provider,
      String providerRef,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {

    return new Payment(
        UUID.randomUUID(), orderId,
        amount, currency,
        status, provider,
        providerRef, createdAt, updatedAt);
  }

  public static Payment rehydrate(
      UUID id,
      UUID orderId,
      BigDecimal amount,
      String currency,
      String status,
      String provider,
      String providerRef,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {

    return new Payment(
        id, orderId,
        amount, currency,
        status, provider,
        providerRef, createdAt, updatedAt);
  }
}
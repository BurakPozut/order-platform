package com.burakpozut.payment_service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.DomainValidationException;

public record Payment(
    UUID id,
    UUID orderId,
    BigDecimal amount,
    Currency currency,
    PaymentStatus status,
    String provider,
    String providerRef,
    String idempotencyKey,
    LocalDateTime updatedAt) {
  public Payment {
    if (id == null)
      throw new DomainValidationException("Payment Id cannot be null");
    if (orderId == null)
      throw new DomainValidationException("Order Id cannot be null");
    if (amount == null)
      throw new DomainValidationException("Amount cannot be null");
    if (amount.compareTo(BigDecimal.ZERO) < 0)
      throw new DomainValidationException("Amount cannot be negative");
    if (currency == null)
      throw new DomainValidationException("Currency cannot be null or blank");
    if (status == null)
      throw new DomainValidationException("Status cannot be null or blank");
    if (provider == null || provider.isBlank())
      throw new DomainValidationException("Provider cannot be null or blank");
  }

  public Payment update(PaymentStatus newStatus, Currency newCurrency,
      String newProvider,
      String newProviderRef) {

    boolean statusChanged = newStatus != null && newStatus != this.status;
    boolean currencyChanged = newCurrency != null && newCurrency != this.currency;
    boolean providerChanged = newProvider != null && !newProvider.equals(this.provider);
    boolean providerRefChanged = newProviderRef != null && !newProviderRef.equals(this.providerRef);

    if (!statusChanged && !currencyChanged && !providerChanged && !providerRefChanged) {
      return this; // This means nothing is changed
    }

    if (statusChanged) {
      validateStatusTransition(this.status, newStatus);
    }
    return Payment.rehydrate(
        this.id,
        this.orderId,
        this.amount,
        currencyChanged ? newCurrency : this.currency,
        statusChanged ? newStatus : this.status,
        providerChanged ? newProvider : this.provider,
        providerRefChanged ? newProviderRef : this.providerRef,
        this.idempotencyKey,
        this.updatedAt);
  }

  private static void validateStatusTransition(PaymentStatus current, PaymentStatus next) {
    boolean isValid = switch (current) {
      case PENDING ->
        next == PaymentStatus.COMPLETED ||
            next == PaymentStatus.FAILED ||
            next == PaymentStatus.CANCELLED;

      case COMPLETED ->
        next == PaymentStatus.REFUNDED;

      case FAILED, CANCELLED, REFUNDED ->
        false;
    };
    if (!isValid) {
      throw new DomainValidationException(
          String.format("Invalid payment status transition from %s to %s", current, next));
    }
  }

  // #region Factory Methods
  public static Payment of(
      UUID orderId,
      BigDecimal amount,
      Currency currency,
      PaymentStatus status,
      String provider,
      String providerRef,
      String idempotencyKey) {

    return new Payment(
        UUID.randomUUID(), orderId,
        amount, currency,
        status, provider,
        providerRef, idempotencyKey, null);
  }

  public static Payment rehydrate(
      UUID id,
      UUID orderId,
      BigDecimal amount,
      Currency currency,
      PaymentStatus status,
      String provider,
      String providerRef,
      String idempotencyKey, LocalDateTime updatedAt) {

    return new Payment(
        id, orderId,
        amount, currency,
        status, provider,
        providerRef, idempotencyKey, updatedAt);
  }
  // #endregion
}

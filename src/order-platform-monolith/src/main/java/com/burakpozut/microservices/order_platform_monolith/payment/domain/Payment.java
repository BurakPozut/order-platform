package com.burakpozut.microservices.order_platform_monolith.payment.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.common.exception.DomainValidationException;

import lombok.Getter;

@Getter
public class Payment {
  private final UUID id;
  private final UUID orderId;
  private final BigDecimal amount;
  private final PaymentStatus status;
  private final String provider;
  private final String providerRef;

  public Payment(UUID id, UUID orderId, BigDecimal amount,
      PaymentStatus status, String provider, String providerRef) {
    if (id == null)
      throw new DomainValidationException("Payment id can not be null");
    if (orderId == null)
      throw new DomainValidationException("Order Id cannot be null");
    if (provider == null)
      throw new DomainValidationException("Provider can not be null");
    if (providerRef == null)
      throw new DomainValidationException("Provider Referance can not be null");

    this.id = id;
    this.orderId = orderId;
    this.amount = amount;
    this.status = status != null ? status : PaymentStatus.PENDING;
    this.provider = provider;
    this.providerRef = providerRef;
  }

  public static Payment createNew(UUID orderId, BigDecimal amount, PaymentStatus status, String provider,
      String providerRef) {
    return new Payment(UUID.randomUUID(), orderId, amount, status, provider, providerRef);
  }

  public static Payment rehydrate(UUID id, UUID orderId, BigDecimal amount, PaymentStatus status, String provider,
      String providerRef) {
    return new Payment(id, orderId, amount, status, provider, providerRef);
  }

}

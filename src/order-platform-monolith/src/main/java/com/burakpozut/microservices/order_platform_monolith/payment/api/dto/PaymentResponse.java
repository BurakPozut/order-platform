package com.burakpozut.microservices.order_platform_monolith.payment.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.payment.domain.Payment;
import com.burakpozut.microservices.order_platform_monolith.payment.domain.PaymentStatus;

public record PaymentResponse(
    UUID id,
    UUID orderId,
    BigDecimal amount,
    PaymentStatus status,
    String provider,
    String providerRef) {

  public static PaymentResponse from(Payment payment) {
    return new PaymentResponse(payment.getId(),
        payment.getOrderId(),
        payment.getAmount(),
        payment.getStatus(),
        payment.getProvider(),
        payment.getProviderRef());
  }
}

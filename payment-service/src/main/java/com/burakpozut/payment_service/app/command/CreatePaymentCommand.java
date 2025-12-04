package com.burakpozut.payment_service.app.command;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.payment_service.domain.PaymentStatus;

public record CreatePaymentCommand(
    UUID orderId,
    BigDecimal amount,
    Currency currency,
    PaymentStatus status,
    String provider,
    String providerRef) {

  public static CreatePaymentCommand of(UUID orderId, BigDecimal amount,
      Currency currency, PaymentStatus status, String provider, String providerRef) {
    return new CreatePaymentCommand(orderId, amount, currency, status, provider, providerRef);
  }
}

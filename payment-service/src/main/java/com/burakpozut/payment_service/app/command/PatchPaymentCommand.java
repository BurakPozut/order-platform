package com.burakpozut.payment_service.app.command;

import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.payment_service.domain.PaymentStatus;

public record PatchPaymentCommand(
    UUID orderId,
    Currency currency,
    PaymentStatus status,
    String provider,
    String providerRef) {
  public static PatchPaymentCommand of(UUID orderId, Currency currency,
      PaymentStatus status, String provider, String providerRef) {
    return new PatchPaymentCommand(orderId, currency, status, provider, providerRef);
  }

}

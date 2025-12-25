package com.burakpozut.payment_service.app.command;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.payment_service.domain.PaymentStatus;

public record PatchPaymentCommand(
    // UUID orderId,
    Currency currency,
    PaymentStatus status,
    String provider,
    String providerRef) {
  public static PatchPaymentCommand of(Currency currency,
      PaymentStatus status, String provider, String providerRef) {
    return new PatchPaymentCommand(currency, status, provider, providerRef);
  }

}

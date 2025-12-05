package com.burakpozut.payment_service.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentStatus;

public record PaymentResponse(
    UUID id,
    UUID orderId,
    BigDecimal amount,
    Currency currency,
    PaymentStatus status,
    String provider,
    String providerRef) {
  public static PaymentResponse from(Payment p) {
    return new PaymentResponse(p.id(), p.orderId(),
        p.amount(), p.currency(), p.status(), p.provider(), p.providerRef());
  }

}

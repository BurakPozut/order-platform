package com.burakpozut.payment_service.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.payment_service.domain.Payment;

public record PaymentResponse(
    UUID id,
    UUID orderId,
    BigDecimal amount,
    String currency,
    String status) {
  public static PaymentResponse from(Payment p) {
    return new PaymentResponse(p.id(), p.orderId(),
        p.amount(), p.currency(), p.status());
  }

}

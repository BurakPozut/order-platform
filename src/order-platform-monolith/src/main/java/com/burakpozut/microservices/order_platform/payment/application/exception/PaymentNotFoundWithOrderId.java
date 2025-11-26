package com.burakpozut.microservices.order_platform.payment.application.exception;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.exception.NotFoundException;

public class PaymentNotFoundWithOrderId extends NotFoundException {
  public PaymentNotFoundWithOrderId(UUID id) {
    super("Payment not found wiht order id: " + id);
  }
}

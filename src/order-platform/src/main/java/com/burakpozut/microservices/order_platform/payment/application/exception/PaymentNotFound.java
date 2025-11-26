package com.burakpozut.microservices.order_platform.payment.application.exception;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.exception.NotFoundException;

public class PaymentNotFound extends NotFoundException {
  public PaymentNotFound(UUID id) {
    super("Payment not found with id: " + id);
  }
}
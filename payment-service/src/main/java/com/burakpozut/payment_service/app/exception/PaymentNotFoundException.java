package com.burakpozut.payment_service.app.exception;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class PaymentNotFoundException extends NotFoundException {
  public PaymentNotFoundException(UUID id) {
    super("Payment not found with id: " + id);
  }

}

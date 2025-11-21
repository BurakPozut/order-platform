package com.burakpozut.microservices.order_platform.customer.application.exception;

import com.burakpozut.microservices.order_platform.common.exception.BusinessException;

public class EmailAlreadyInUseException extends BusinessException {
  public EmailAlreadyInUseException(String message) {
    super("Email is already in use: " + message);
  }
}

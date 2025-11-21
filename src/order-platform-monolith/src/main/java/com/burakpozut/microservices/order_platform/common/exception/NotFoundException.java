package com.burakpozut.microservices.order_platform.common.exception;

public abstract class NotFoundException extends AppException {

  public NotFoundException(String message) {
    super(message);
  }
}

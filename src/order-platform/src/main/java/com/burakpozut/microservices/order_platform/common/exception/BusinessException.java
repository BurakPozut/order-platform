package com.burakpozut.microservices.order_platform.common.exception;

public abstract class BusinessException extends AppException {

  public BusinessException(String message) {
    super(message);
  }
}

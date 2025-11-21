package com.burakpozut.microservices.order_platform.common.exception;

public abstract class AppException extends RuntimeException {
  public AppException(String message) {
    super(message);
  }

  public AppException(String message, Throwable cause) {
    super(message, cause);
  }

}

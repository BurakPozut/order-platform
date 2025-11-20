package com.burakpozut.microservices.order_platform_monolith.common.exception;

public class DomainValidationException extends RuntimeException {

  public DomainValidationException(String message) {
    super(message);
  }
}

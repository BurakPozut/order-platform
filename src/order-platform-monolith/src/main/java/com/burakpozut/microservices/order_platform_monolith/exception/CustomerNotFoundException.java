package com.burakpozut.microservices.order_platform_monolith.exception;

public class CustomerNotFoundException extends RuntimeException {
  public CustomerNotFoundException(String message) {
    super(message);
  }

}

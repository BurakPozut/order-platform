package com.burakpozut.microservices.order_platform.customer.application.exception;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.exception.NotFoundException;

public class CustomerNotFoundException extends NotFoundException {

  public CustomerNotFoundException(String email) {
    super("Customer not found with email: " + email);
  }

  public CustomerNotFoundException(UUID id) {
    super("Customer not found with id: " + id);
  }
}

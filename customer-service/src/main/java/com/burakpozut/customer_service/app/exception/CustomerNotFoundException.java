package com.burakpozut.customer_service.app.exception;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class CustomerNotFoundException extends NotFoundException {
  public CustomerNotFoundException(String email) {
    super("Customer not found with email: " + email);
  }

  public CustomerNotFoundException(UUID id) {
    super("Customer not found with id: " + id);
  }

}

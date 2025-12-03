package com.burakpozut.order_service.app.exception.customer;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class CustomerNotFoundException extends NotFoundException {
  public CustomerNotFoundException(UUID id) {
    super("Customer not found with id: " + id);
  }

}

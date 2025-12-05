package com.burakpozut.payment_service.app.exception;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

  public OrderNotFoundException(UUID id) {
    super("Order not found with id: " + id);
  }

}

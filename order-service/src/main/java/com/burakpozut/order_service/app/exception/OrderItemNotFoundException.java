package com.burakpozut.order_service.app.exception;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class OrderItemNotFoundException extends NotFoundException {
  public OrderItemNotFoundException(UUID orderItemId) {
    super("Order Item not found with id: " + orderItemId);
  }

}

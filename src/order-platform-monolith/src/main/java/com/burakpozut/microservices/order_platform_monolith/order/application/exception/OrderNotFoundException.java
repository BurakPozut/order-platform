package com.burakpozut.microservices.order_platform_monolith.order.application.exception;

import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.common.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

  public OrderNotFoundException(UUID id) {
    super("Order not found with id: " + id);
  }
}

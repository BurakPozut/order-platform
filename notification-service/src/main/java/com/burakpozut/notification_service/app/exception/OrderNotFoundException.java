package com.burakpozut.notification_service.app.exception;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {
  public OrderNotFoundException(UUID orderId) {
    super("Order not found with id: " + orderId);
  }

}

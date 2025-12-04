package com.burakpozut.order_service.app.command;

import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.domain.OrderStatus;

public record UpdateOrderCommand(
    UUID customerId,
    OrderStatus status,
    Currency currency) {
  public static UpdateOrderCommand of(
      UUID customerId,
      OrderStatus status,
      Currency currency) {
    return new UpdateOrderCommand(customerId, status, currency);
  }
}

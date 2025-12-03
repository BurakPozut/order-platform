package com.burakpozut.order_service.app.command;

import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.domain.OrderStatus;

public record UpdateOrderCommand(
    UUID customerId,
    OrderStatus status,
    Currency currency) {
  public static UpdateOrderCommand of(
      UUID productId,
      OrderStatus status,
      Currency currency) {
    return new UpdateOrderCommand(productId, status, currency);
  }
}

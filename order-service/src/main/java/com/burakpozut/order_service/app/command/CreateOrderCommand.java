package com.burakpozut.order_service.app.command;

import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.domain.OrderStatus;

public record CreateOrderCommand(
    UUID customerId,
    OrderStatus status,
    Currency currency,
    List<OrderItemData> items) {

  public static CreateOrderCommand of(UUID customerId, OrderStatus status,
      Currency currency, List<OrderItemData> items) {
    return new CreateOrderCommand(customerId, status, currency, items);
  }

}

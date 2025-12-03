package com.burakpozut.order_service.app.command;

import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.api.dto.request.OrderItemRequest;
import com.burakpozut.order_service.domain.OrderStatus;

public record CreateOrderCommand(
    UUID customerId,
    OrderStatus status,
    Currency currency,
    List<OrderItemRequest> items) {
  public static CreateOrderCommand of(UUID customerId, OrderStatus status,
      Currency currency, List<OrderItemRequest> items) {
    return new CreateOrderCommand(customerId, status, currency, items);
  }

}

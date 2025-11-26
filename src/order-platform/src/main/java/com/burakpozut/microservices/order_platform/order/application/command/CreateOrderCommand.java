package com.burakpozut.microservices.order_platform.order.application.command;

import java.util.List;
import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.domain.Currency;
import com.burakpozut.microservices.order_platform.order.domain.OrderStatus;

public record CreateOrderCommand(
    UUID customerId,
    OrderStatus status,
    Currency currency,
    List<OrderItemData> items) {

  public record OrderItemData(
      UUID productId,
      Integer quantity) {
  }
}

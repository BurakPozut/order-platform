package com.burakpozut.order_service.app.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.domain.OrderItem;
import com.burakpozut.order_service.domain.OrderStatus;

public record CreateOrderCommand(
    UUID customerId,
    OrderStatus status,
    BigDecimal totalAmount,
    Currency currency,
    List<OrderItem> items) {
  public static CreateOrderCommand of(UUID customerId, OrderStatus status,
      BigDecimal totalAmount, Currency currency, List<OrderItem> items) {
    return new CreateOrderCommand(customerId, status, totalAmount, currency, items);
  }

}

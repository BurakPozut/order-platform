package com.burakpozut.order_service.api.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderStatus;

public record OrderResponse(
    UUID id,
    UUID customerId,
    OrderStatus status,
    BigDecimal totalAmount,
    Currency currency,
    List<OrderItemResponse> items

) {

  public static OrderResponse from(Order o) {
    var items = o.items().stream()
        .map(OrderItemResponse::from).collect(Collectors.toList());
    return new OrderResponse(o.id(), o.customerId(),
        o.status(), o.totalAmount(),
        o.currency(), items);
  }
}

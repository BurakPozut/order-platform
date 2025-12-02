package com.burakpozut.order_service.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderStatus;

public record OrderResponse(
    UUID id,
    UUID customerId,
    OrderStatus status,
    BigDecimal totalAmount,
    Currency currency

) {

  public static OrderResponse from(Order o) {
    return new OrderResponse(o.id(), o.customerId(), o.status(), o.totalAmount(), o.currency());
  }
}

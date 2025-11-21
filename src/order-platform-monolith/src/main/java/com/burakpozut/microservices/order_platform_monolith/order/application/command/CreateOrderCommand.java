package com.burakpozut.microservices.order_platform_monolith.order.application.command;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.common.domain.Currency;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateOrderCommand {
  private final UUID customerId;
  private final OrderStatus status;
  private final BigDecimal totalAmount;
  private final Currency currency;
}

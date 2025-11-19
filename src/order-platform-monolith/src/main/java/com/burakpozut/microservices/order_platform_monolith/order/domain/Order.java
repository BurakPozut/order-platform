package com.burakpozut.microservices.order_platform_monolith.order.domain;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Order {
  private final UUID id;
  private final UUID customerId;
  private final OrderStatus orderStatus;
}

package com.burakpozut.microservices.order_platform_monolith.order.api.dto;

import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {
  private UUID id;
  private UUID customerId;
  private OrderStatus status;
}

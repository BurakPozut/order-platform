package com.burakpozut.microservices.order_platform.order.application.query;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetOrderDetailsQuery {
  private final UUID orderId;
}

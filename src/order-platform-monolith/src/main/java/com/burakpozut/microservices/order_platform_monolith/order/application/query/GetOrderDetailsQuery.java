package com.burakpozut.microservices.order_platform_monolith.order.application.query;

import java.util.UUID;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetOrderDetailsQuery {
  private final UUID orderId;
}

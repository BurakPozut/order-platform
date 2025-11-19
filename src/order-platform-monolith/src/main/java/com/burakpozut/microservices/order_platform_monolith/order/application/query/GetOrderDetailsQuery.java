package com.burakpozut.microservices.order_platform_monolith.order.application.query;

import java.util.UUID;

import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetOrderDetailsQuery {
  @NonNull
  private final UUID orderId;
}

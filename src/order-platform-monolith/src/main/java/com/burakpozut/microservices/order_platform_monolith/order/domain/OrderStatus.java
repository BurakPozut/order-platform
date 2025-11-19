package com.burakpozut.microservices.order_platform_monolith.order.domain;

public enum OrderStatus {
  PENDING,
  CONFIRMED,
  PROCESSING,
  SHIPPED,
  DELIVERED,
  CANCELLED
}

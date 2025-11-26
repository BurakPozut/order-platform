package com.burakpozut.microservices.order_platform.order.domain;

public enum OrderStatus {
  PENDING,
  CONFIRMED,
  PROCESSING,
  SHIPPED,
  DELIVERED,
  CANCELLED
}

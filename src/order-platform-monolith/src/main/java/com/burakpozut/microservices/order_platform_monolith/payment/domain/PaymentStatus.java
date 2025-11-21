package com.burakpozut.microservices.order_platform_monolith.payment.domain;

public enum PaymentStatus {
  PENDING,
  COMPLETED,
  FAILED,
  CANCELLED,
  REFUNDED
}

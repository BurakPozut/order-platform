package com.burakpozut.microservices.order_platform.payment.domain;

public enum PaymentStatus {
  PENDING,
  COMPLETED,
  FAILED,
  CANCELLED,
  REFUNDED
}

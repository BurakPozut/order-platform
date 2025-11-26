package com.burakpozut.microservices.order_platform.notification.domain;

public enum NotificationType {
  ORDER_CONFIRMATION,
  SHIPPING_UPDATE,
  DELIVERY_CONFIRMATION,
  ORDER_CANCELLATION,
  PAYMENT_RECEIPT,
  PROMOTIONAL,
  PASSWORD_RESET,
  ACCOUNT_ALERT,
  PAYMENT_DUE,
  PAYMENT_CONFIRMED
}
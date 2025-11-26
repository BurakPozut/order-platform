package com.burakpozut.microservices.order_platform.payment.domain.port;

import java.util.UUID;

public interface NotificationGateway {
  void createNotificationForPayment(UUID customerId, UUID orderId, String type, String channel, String status);
}

package com.burakpozut.microservices.order_platform.order.domain.port;

import java.util.UUID;

public interface NotificationGateway {
  void createNotificationForOrder(UUID customerId, UUID orderId, String type, String channel, String status);
}

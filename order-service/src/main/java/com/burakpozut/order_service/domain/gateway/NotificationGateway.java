package com.burakpozut.order_service.domain.gateway;

import java.util.UUID;

public interface NotificationGateway {
  void sendNotification(UUID customerId, UUID orderId);
}

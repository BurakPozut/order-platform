package com.burakpozut.notification_service.domain.gateway;

import java.util.UUID;

public interface OrderGateway {
  UUID getOrderCustomerId(UUID orderId);
}

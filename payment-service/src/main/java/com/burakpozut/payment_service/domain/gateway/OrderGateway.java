package com.burakpozut.payment_service.domain.gateway;

import java.util.UUID;

public interface OrderGateway {
  boolean validateOrderId(UUID orderId);
}

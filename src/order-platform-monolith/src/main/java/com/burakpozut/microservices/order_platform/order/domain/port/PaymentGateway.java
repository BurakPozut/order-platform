package com.burakpozut.microservices.order_platform.order.domain.port;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentGateway {
  void createPaymentForOrder(UUID orderId, BigDecimal amount, String provider, String providerRef);
}

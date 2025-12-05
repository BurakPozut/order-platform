package com.burakpozut.order_service.domain.gateway;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;

public interface PaymentGateway {
  void createPayment(UUID orderId, BigDecimal amount, Currency currency,
      String provider, String providerRef);

}

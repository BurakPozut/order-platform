package com.burakpozut.microservices.order_platform_monolith.payment.domain.port;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface OrderGateway {
  Optional<BigDecimal> getOrderAmount(UUID id);

}

package com.burakpozut.microservices.order_platform_monolith.order.domain.port;

import java.util.UUID;

public interface CustomerGateway {
  boolean customerExists(UUID id);
}

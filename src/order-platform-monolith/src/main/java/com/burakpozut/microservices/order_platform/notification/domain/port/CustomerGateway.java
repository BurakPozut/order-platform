package com.burakpozut.microservices.order_platform.notification.domain.port;

import java.util.UUID;

public interface CustomerGateway {
  boolean customerExists(UUID id);
}

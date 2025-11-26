package com.burakpozut.microservices.order_platform.notification.domain.port;

import java.util.UUID;

public interface OrderGateway {

  boolean orderExists(UUID id);
}

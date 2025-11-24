package com.burakpozut.microservices.order_platform.notification.domain.port;

import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderGateway {

  boolean orderExists(UUID id);
}

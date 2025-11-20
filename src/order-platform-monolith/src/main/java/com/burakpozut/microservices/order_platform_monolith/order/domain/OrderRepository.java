package com.burakpozut.microservices.order_platform_monolith.order.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {
  Optional<Order> findById(UUID id);
}

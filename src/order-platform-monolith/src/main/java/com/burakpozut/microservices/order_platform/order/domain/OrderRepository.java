package com.burakpozut.microservices.order_platform.order.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {
  Optional<Order> findById(UUID id);

  List<Order> findAll();

  Order save(Order order, boolean isNew);
}

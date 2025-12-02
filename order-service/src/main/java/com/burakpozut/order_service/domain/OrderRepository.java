package com.burakpozut.order_service.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
  Optional<Order> findById(UUID id);

  List<Order> findAll();

  Order save(Order order, boolean isNew);

  void deleteById(UUID id);
}

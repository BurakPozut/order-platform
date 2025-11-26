package com.burakpozut.microservices.order_platform.order.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository {
  Optional<OrderItem> findById(UUID id);

  void saveAll(List<OrderItem> orderItems, boolean isNew);

}

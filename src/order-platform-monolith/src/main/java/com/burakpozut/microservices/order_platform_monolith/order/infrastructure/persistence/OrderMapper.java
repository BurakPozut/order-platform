package com.burakpozut.microservices.order_platform_monolith.order.infrastructure.persistence;

import org.springframework.lang.NonNull;

import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;

public class OrderMapper {
  @NonNull
  static Order toDomain(OrderJpaEntity entity) {
    return new Order(entity.getId(), entity.getCustomerId(), entity.getStatus());
  }

}

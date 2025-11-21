package com.burakpozut.microservices.order_platform_monolith.order.infrastructure.persistence;

import com.burakpozut.microservices.order_platform_monolith.common.domain.Currency;
import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;

public class OrderMapper {
  static Order toDomain(OrderJpaEntity entity) {
    return Order.rehydrate(entity.getId(), entity.getCustomerId(), entity.getStatus(), entity.getTotalAmount(),
        Currency.valueOf(entity.getCurrency()));
  }

  static OrderJpaEntity toEntity(Order order, boolean isNew) {
    var entity = new OrderJpaEntity();
    entity.setId(order.getId());
    entity.setCustomerId(order.getCustomerId());
    entity.setStatus(order.getOrderStatus());
    entity.setTotalAmount(order.getTotalAmount());
    entity.setCurrency(order.getCurrency().name());
    return entity;
  }

}

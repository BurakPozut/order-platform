package com.burakpozut.microservices.order_platform.order.infrastructure.persistence;

import com.burakpozut.microservices.order_platform.order.domain.OrderItem;

public class OrderItemMapper {
  static OrderItem toDomain(OrderItemJpaEntity entity) {
    return OrderItem.rehydrate(entity.getId(), entity.getOrderId(), entity.getProductId(), entity.getProductName(),
        entity.getUnitPrice(), entity.getQuantity());
  }

  static OrderItemJpaEntity toEntity(OrderItem orderItem, boolean isNew) {
    var entity = new OrderItemJpaEntity();
    entity.setId(orderItem.getId());
    entity.setOrderId(orderItem.getOrderId());
    entity.setProductId(orderItem.getProductId());
    entity.setProductName(orderItem.getProductName());
    entity.setUnitPrice(orderItem.getUnitPrice());
    entity.setQuantity(orderItem.getQuantity());
    entity.setNew(isNew);
    return entity;
  }
}

package com.burakpozut.order_service.infra.persistance;

import com.burakpozut.order_service.domain.Order;

public class OrderMapper {
  public static Order toDomain(OrderJpaEntity entity) {
    return Order.rehydrate(entity.getId(), entity.getCustomerId(), entity.getStatus(), entity.getTotalAmount(),
        entity.getCurrency());
  }

  public static OrderJpaEntity toEntity(Order o, boolean isNew) {
    var entity = new OrderJpaEntity();
    entity.setId(o.id());
    entity.setCustomerId(o.customerId());
    entity.setStatus(o.status());
    entity.setTotalAmount(o.totalAmount());
    entity.setCurrency(o.currency());
    entity.setNew(isNew);
    return entity;
  }

}

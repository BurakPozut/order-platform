package com.burakpozut.order_service.infra.persistance;

import java.util.stream.Collectors;

import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderItem;

public class OrderMapper {
  public static Order toDomain(OrderJpaEntity entity) {
    var items = entity.getItems().stream()
        .map(item -> OrderItem.rehydrate(item.getId(),
            item.getProductId(), item.getProductName(),
            item.getUnitPrice(), item.getQuantity()))
        .collect(Collectors.toList());

    return Order.rehydrate(entity.getId(), entity.getCustomerId(),
        entity.getStatus(), entity.getTotalAmount(),
        entity.getCurrency(), items, entity.getIdempotencyKey(), entity.getUpdatedAt());
  }

  public static OrderJpaEntity toEntity(Order order, boolean isNew) {
    var entity = new OrderJpaEntity();
    entity.setId(order.id());
    entity.setCustomerId(order.customerId());
    entity.setStatus(order.status());
    entity.setTotalAmount(order.totalAmount());
    entity.setCurrency(order.currency());
    entity.setIdempotencyKey(order.idempotencyKey());
    entity.setNew(isNew);

    // Map items
    var itemEntities = order.items().stream()
        .map(item -> {
          var itemEntity = new OrderItemJpaEntity();
          itemEntity.setId(item.id());
          itemEntity.setOrder(entity); // Set the relationship
          itemEntity.setProductId(item.productId());
          itemEntity.setProductName(item.productName());
          itemEntity.setUnitPrice(item.unitPrice());
          itemEntity.setQuantity(item.quantity());
          return itemEntity;
        })
        .collect(Collectors.toList());

    entity.setItems(itemEntities);
    return entity;
  }

}

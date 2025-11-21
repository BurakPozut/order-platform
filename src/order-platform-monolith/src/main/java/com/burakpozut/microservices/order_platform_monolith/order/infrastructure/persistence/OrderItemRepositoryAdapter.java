package com.burakpozut.microservices.order_platform_monolith.order.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderItem;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderItemRepository;

import lombok.RequiredArgsConstructor;

@Repository
@Primary
@RequiredArgsConstructor
public class OrderItemRepositoryAdapter implements OrderItemRepository {
  private final SpringDataOrderItemRepository jpa;

  @Override
  public Optional<OrderItem> findById(UUID id) {
    return jpa.findById(id).map(OrderItemMapper::toDomain);
  }

  @Override
  public void saveAll(List<OrderItem> orderItems, boolean isNew) {
    var entities = orderItems.stream().map(item -> OrderItemMapper.toEntity(item, isNew)).collect(Collectors.toList());
    jpa.saveAll(entities);
  }
}

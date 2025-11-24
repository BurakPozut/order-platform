package com.burakpozut.microservices.order_platform.order.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.order.domain.Order;
import com.burakpozut.microservices.order_platform.order.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Primary
@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

  private final SpringDataOrderRepository jpa;

  @Override
  public Optional<Order> findById(UUID id) {
    return jpa.findById(id).map(OrderMapper::toDomain);
  }

  @Override
  public Order save(Order order, boolean isNew) {
    var entity = OrderMapper.toEntity(order, isNew);
    var savedEntity = jpa.save(entity);
    return OrderMapper.toDomain(savedEntity);
  }

  @Override
  public List<Order> findAll() {
    return jpa.findAll().stream().map(OrderMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    jpa.deleteById(id);
  }
}

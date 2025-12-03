package com.burakpozut.order_service.infra.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {
  private final SpringDataOrderRepository jpa;

  @Override
  public void deleteById(UUID id) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<Order> findAll() {
    return jpa.findAll().stream().map(OrderMapper::toDomain).collect(Collectors.toList());
  }

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
}

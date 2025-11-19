package com.burakpozut.microservices.order_platform_monolith.order.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

  private final SpringDataOrderRepository jpa;

  @Override
  public Optional<Order> findById(@NonNull UUID id) {
    return jpa.findById(id).map(OrderMapper::toDomain);
  }

}

package com.burakpozut.order_service.infra.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    jpa.deleteById(id);
  }

  @Override
  public List<Order> findAll() {
    return jpa.findAll().stream().map(OrderMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Slice<Order> findAll(Pageable pageable) {
    Slice<OrderJpaEntity> entitySlice = jpa.findAll(pageable);
    List<Order> orders = entitySlice.getContent().stream().map(OrderMapper::toDomain).toList();
    return new SliceImpl<Order>(orders, pageable, entitySlice.hasNext());
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

  @Override
  public Optional<Order> findByIdempotencyKey(String idempotencyKey) {
    return jpa.findByIdempotencyKey(idempotencyKey).map(OrderMapper::toDomain);
  }
}

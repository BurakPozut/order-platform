package com.burakpozut.order_service.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.burakpozut.order_service.domain.Order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface OrderRepository {
    Optional<Order> findById(UUID id);

    List<Order> findAll();

    Slice<Order> findAll(Pageable pageable);

    Order save(Order order, boolean isNew);

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    void deleteById(UUID id);
}

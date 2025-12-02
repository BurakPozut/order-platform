package com.burakpozut.order_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.app.exception.OrderNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetOrderByIdService {
  private final OrderRepository orderRepository;

  public Order handle(UUID id) {
    var order = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));

    return order;
  }

}

package com.burakpozut.microservices.order_platform.order.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.order.application.exception.OrderNotFoundException;
import com.burakpozut.microservices.order_platform.order.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteOrderService {
  private final OrderRepository orderRepository;

  @Transactional
  public void handle(UUID id) {
    if (!orderRepository.findById(id).isPresent())
      throw new OrderNotFoundException(id);
    orderRepository.deleteById(id);
  }

}

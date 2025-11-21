package com.burakpozut.microservices.order_platform_monolith.order.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllOrdersService {
  private final OrderRepository orderRepository;

  public List<Order> handle() {
    return orderRepository.findAll();
  }

}

package com.burakpozut.microservices.order_platform_monolith.order.application.service;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform_monolith.order.api.exception.OrderNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.order.application.query.GetOrderDetailsQuery;
import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetOrderDetailsService {
  private final OrderRepository orderRepository;

  public Order handle(GetOrderDetailsQuery query) {
    return orderRepository.findById(query.getOrderId())
        .orElseThrow(() -> new OrderNotFoundException(query.getOrderId()));
  }

}

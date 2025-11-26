package com.burakpozut.microservices.order_platform.order.application.service;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform.order.application.exception.OrderNotFoundException;
import com.burakpozut.microservices.order_platform.order.application.query.GetOrderDetailsQuery;
import com.burakpozut.microservices.order_platform.order.domain.Order;
import com.burakpozut.microservices.order_platform.order.domain.OrderRepository;

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

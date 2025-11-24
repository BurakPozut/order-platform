package com.burakpozut.microservices.order_platform.notification.infrastructure.order;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.notification.domain.port.OrderGateway;
import com.burakpozut.microservices.order_platform.order.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Repository("notificationOrderGatewayAdapter")
@RequiredArgsConstructor
public class OrderGatewayAdapter implements OrderGateway {
  private final OrderRepository orderRepository;

  @Override
  public boolean orderExists(UUID id) {
    return orderRepository.findById(id).isPresent();
  }
}

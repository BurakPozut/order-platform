package com.burakpozut.microservices.order_platform_monolith.payment.infrastructure.order;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderRepository;
import com.burakpozut.microservices.order_platform_monolith.payment.domain.port.OrderGateway;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderGatewayAdapter implements OrderGateway {
  private final OrderRepository orderRepository;

  @Override
  public Optional<BigDecimal> getOrderAmount(UUID id) {
    return orderRepository.findById(id).map(Order::getTotalAmount);
  }

}

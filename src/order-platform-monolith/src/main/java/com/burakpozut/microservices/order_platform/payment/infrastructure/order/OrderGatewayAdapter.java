package com.burakpozut.microservices.order_platform.payment.infrastructure.order;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.order.domain.Order;
import com.burakpozut.microservices.order_platform.order.domain.OrderRepository;
import com.burakpozut.microservices.order_platform.payment.domain.port.OrderGateway;

import lombok.RequiredArgsConstructor;

@Repository("paymentOrderGatewayAdapter")
@RequiredArgsConstructor
public class OrderGatewayAdapter implements OrderGateway {
  private final OrderRepository orderRepository;

  @Override
  public Optional<BigDecimal> getOrderAmount(UUID id) {
    return orderRepository.findById(id).map(Order::getTotalAmount);
  }

}

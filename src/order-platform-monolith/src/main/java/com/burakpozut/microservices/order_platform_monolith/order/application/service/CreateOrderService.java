package com.burakpozut.microservices.order_platform_monolith.order.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform_monolith.customer.application.exception.CustomerNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.order.application.command.CreateOrderCommand;
import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderRepository;
import com.burakpozut.microservices.order_platform_monolith.order.domain.port.CustomerGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateOrderService {
  private final OrderRepository orderRepository;
  private final CustomerGateway customerGateway;

  @Transactional
  public Order handle(CreateOrderCommand command) {
    if (!customerGateway.customerExists(command.getCustomerId()))
      throw new CustomerNotFoundException(command.getCustomerId());
    var order = Order.createNew(command.getCustomerId(), command.getStatus(), command.getTotalAmount(),
        command.getCurrency());
    return orderRepository.save(order, true);
  }

}

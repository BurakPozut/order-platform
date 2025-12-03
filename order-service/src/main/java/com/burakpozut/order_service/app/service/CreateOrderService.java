package com.burakpozut.order_service.app.service;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.app.command.CreateOrderCommand;
import com.burakpozut.order_service.app.exception.CustomerNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderRepository;
import com.burakpozut.order_service.domain.gateway.CustomerGateway;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderService {
  private final OrderRepository orderRepository;
  private final CustomerGateway customerGateway;

  @Transactional
  public Order handle(CreateOrderCommand command) {
    if (!customerGateway.validateCustomerExists(command.customerId())) {
      throw new CustomerNotFoundException(command.customerId());
    }
    log.info("the customer is ok: {}", command.customerId());

    Order order = Order.of(command.customerId(), command.status(), command.totalAmount(), command.currency(),
        command.items());
    return orderRepository.save(order, true);
  }

}

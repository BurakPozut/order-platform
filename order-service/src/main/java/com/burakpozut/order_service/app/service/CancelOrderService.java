package com.burakpozut.order_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.app.exception.OrderNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderRepository;
import com.burakpozut.order_service.domain.OrderStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelOrderService {
  private final OrderRepository orderRepository;

  @Transactional
  public void handle(UUID orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));

    log.info("Cancelling order : {}", orderId);

    Order cancelledOrder = Order.rehydrate(orderId, order.customerId(),
        OrderStatus.CANCELLED, order.totalAmount(),
        order.currency(), order.items(),
        order.idempotencyKey(), null);

    orderRepository.save(cancelledOrder, false);
  }

}

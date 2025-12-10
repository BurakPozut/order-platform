package com.burakpozut.order_service.app.service.create;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.gateway.NotificationGateway;
import com.burakpozut.order_service.domain.gateway.PaymentGateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSideEffectsService {
  private final PaymentGateway paymentGateway;
  private final NotificationGateway notificationGateway;

  public void trigger(Order order) {
    paymentGateway.createPayment(order.id(),
        order.totalAmount(), order.currency(),
        "PayPal", "pypl-123");

    notificationGateway.sendNotification(order.customerId(),
        order.id());
  }

}

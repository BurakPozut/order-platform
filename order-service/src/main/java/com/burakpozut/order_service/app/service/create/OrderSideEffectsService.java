package com.burakpozut.order_service.app.service.create;

import org.springframework.stereotype.Service;

// import com.burakpozut.common.event.OrderConfirmedEvent;
import com.burakpozut.order_service.domain.Order;
// import com.burakpozut.order_service.domain.gateway.NotificationGateway;
import com.burakpozut.order_service.domain.gateway.PaymentGateway;
import com.burakpozut.order_service.domain.gateway.ProductGateway;
import com.burakpozut.order_service.infra.kafka.OrderConfirmedPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSideEffectsService {
  private final PaymentGateway paymentGateway;
  // private final NotificationGateway notificationGateway;
  private final ProductGateway productGateway;

  private final OrderConfirmedPublisher orderConfirmedPublisher;

  public void trigger(Order order) {
    for (var item : order.items()) {
      productGateway.reserveInventory(item.productId(), item.quantity());
    }

    paymentGateway.createPayment(order.id(),
        order.totalAmount(), order.currency(),
        "PayPal", "pypl-123");

    // notificationGateway.sendNotification(order.customerId(),
    // order.id());
    orderConfirmedPublisher.publish(order);
  }

}

package com.burakpozut.order_service.infra.kafka;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderEvent;
import com.burakpozut.order_service.app.service.CancelOrderService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompensationListener {
  private final CancelOrderService cancelOrderService;

  @KafkaListener(topics = "${app.kafka.topics.order-events}", groupId = "order-service")
  public void onMessage(@Payload OrderEvent event) {
    if (event instanceof OrderCompensationEvent compensationEvent) {

      try {
        log.warn("Received componsation event for order: {}, Reason: {}",
            event.orderId(), compensationEvent.reason());
        cancelOrderService.handle(event.orderId());
      } catch (Exception e) {
        log.error("Failed to process compensation event for order: {}, Error: {}",
            event.orderId(), e.getMessage(), e);
        // TODO: we should warn the client that their order is undo
        // But this is not a critical function and this is just a dummy phase we dont
        // undo a order just for the notification failed

      }
    }
  }
}

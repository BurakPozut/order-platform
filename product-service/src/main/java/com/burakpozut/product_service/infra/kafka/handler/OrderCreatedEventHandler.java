package com.burakpozut.product_service.infra.kafka.handler;

import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.product_service.app.command.ReserveInventoryCommand;
import com.burakpozut.product_service.app.service.RerserveInventoryService;
import com.burakpozut.product_service.infra.kafka.OrderCompensationPublisher;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventHandler {
  private final RerserveInventoryService rerserveInventoryService;
  private final OrderCompensationPublisher orderCompensationPublisher;

  public void handle(OrderCreatedEvent createdEvent) {
    log.info("Processing OrderCreatedEvent for order: {}", createdEvent.orderId());

    try {
      for (OrderItemEvent item : createdEvent.items()) {
        var command = ReserveInventoryCommand.of(item.productId(), item.quantity());
        rerserveInventoryService.handle(command);
        log.info("Successfully reserved inventory for order: {}. Product: {}.",
            createdEvent.orderId(), item.productId());
      }
    } catch (Exception e) {
      log.error("Failed to reserve product for Order: {}. Reason: {}",
          createdEvent.orderId(), e.getMessage(), e);
      orderCompensationPublisher.publish(createdEvent.orderId(),
          createdEvent.customerId(),
          createdEvent.items(),
          "Inventory reservation failed: " + e.getMessage());
    }
  }
}
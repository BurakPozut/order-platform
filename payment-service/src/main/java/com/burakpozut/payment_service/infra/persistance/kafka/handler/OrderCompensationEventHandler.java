package com.burakpozut.payment_service.infra.persistance.kafka.handler;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.payment_service.app.service.CompensatePaymentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompensationEventHandler {

  private final CompensatePaymentService compensatePaymentService;

  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;

  public void handle(OrderCompensationEvent compensationEvent) {
    if (skipIfInitiatedByThisService(compensationEvent)) {
      return;
    }

    try {
      log.warn("Received compensation event for order: {}, Reason: {}",
          compensationEvent.orderId(), compensationEvent.reason());

      compensatePaymentService.handle(compensationEvent.orderId(), compensationEvent.reason());
    } catch (Exception e) {
      log.error("Failed to process compensation event for order: {}, Error: {}",
          compensationEvent.orderId(), e.getMessage(), e);
    }
  }

  private boolean skipIfInitiatedByThisService(OrderCompensationEvent event) {
    if (groupId.equals(event.groupId())) {
      log.debug("Skipping compensation event initiated by this service for order: {}",
          event.orderId());
      return true;
    }
    return false;
  }
}

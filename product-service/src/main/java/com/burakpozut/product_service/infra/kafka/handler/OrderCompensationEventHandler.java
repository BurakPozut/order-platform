package com.burakpozut.product_service.infra.kafka.handler;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.product_service.app.command.ReleaseInventoryCommand;
import com.burakpozut.product_service.app.service.ReleaseInventoryService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompensationEventHandler {
    private final ReleaseInventoryService releaseInventoryService;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    public void handle(OrderCompensationEvent compensationEvent) {
        if (skipIfInitiatedByThisService(compensationEvent)) {
            return;
        }

        try {
            log.warn("Received compensation event for order: {}, Reason: {}, Initiated by service: {}",
                    compensationEvent.orderId(), compensationEvent.reason(), compensationEvent.groupId());

            if (compensationEvent.items() == null || compensationEvent.items().isEmpty()) {
                log.warn("Skipping compensation event for order: {} - items list is null or empty",
                        compensationEvent.orderId());
                return;
            }

            for (OrderItemEvent item : compensationEvent.items()) {
                var command = ReleaseInventoryCommand.of(item.productId(), item.quantity());
                releaseInventoryService.handle(command);
            }
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
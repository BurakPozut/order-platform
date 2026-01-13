package com.burakpozut.product_service.infra.kafka.handler;

import com.burakpozut.common.event.order.OrderCancelledEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.product_service.app.command.ReleaseInventoryCommand;
import com.burakpozut.product_service.app.service.ReleaseInventoryService;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledEventHandler {
    private final ReleaseInventoryService releaseInventoryService;

    public void handle(OrderCancelledEvent cancelledEvent) {
        log.info("Processing OrderCancelledEvent for order: {}, reason: {}",
                cancelledEvent.orderId(), cancelledEvent.reason());
        try {

            if (cancelledEvent.items() == null || cancelledEvent.items().isEmpty()) {
                log.warn("Skipping cancellation event for order: {} - items list is null or empty",
                        cancelledEvent.orderId());
                return;
            }

            for (OrderItemEvent item : cancelledEvent.items()) {
                var command = ReleaseInventoryCommand.of(item.productId(), item.quantity());
                releaseInventoryService.handle(command);
                log.info("Successfully released inventory for order: {}. Product: {}, Quantity: {}",
                        cancelledEvent.orderId(), item.productId(), item.quantity());

            }
            log.info("Successfully processed cancellation for order: {}", cancelledEvent.orderId());
        } catch (Exception e) {
            log.error("Failed to process cancellation for order: {}, Error: {}",
                    cancelledEvent.orderId(), e.getMessage(), e);
        }
    }

}

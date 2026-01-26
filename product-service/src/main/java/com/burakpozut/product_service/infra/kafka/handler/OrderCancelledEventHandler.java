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
        log.info("product.orderCancelled.received orderId={} reason={}",
                cancelledEvent.orderId(), cancelledEvent.reason());
        try {

            if (cancelledEvent.items() == null || cancelledEvent.items().isEmpty()) {
                log.warn("product.orderCancelled.empty_items orderId={} action=skipping",
                        cancelledEvent.orderId());
                return;
            }

            for (OrderItemEvent item : cancelledEvent.items()) {
                var command = ReleaseInventoryCommand.of(item.productId(), item.quantity());
                releaseInventoryService.handle(command);
                log.debug("product.orderCancelled.inventory_released orderId={} productId={} quantity={}",
                        cancelledEvent.orderId(), item.productId(), item.quantity());

            }
            log.info("product.orderCancelled.completed orderId={} itemCount={}",
                    cancelledEvent.orderId(), cancelledEvent.items().size());
        } catch (Exception e) {
            log.error("product.orderCancelled.failed orderId={} message={}",
                    cancelledEvent.orderId(), e.getMessage(), e);
        }
    }

}

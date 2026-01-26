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
            log.warn("product.compensation.received orderId={} reason={} initiatedBy={}",
                    compensationEvent.orderId(), compensationEvent.reason(), compensationEvent.groupId());

            if (compensationEvent.items() == null || compensationEvent.items().isEmpty()) {
                log.warn("product.compensation.empty_items orderId={} action=skipping",
                        compensationEvent.orderId());
                return;
            }

            for (OrderItemEvent item : compensationEvent.items()) {
                var command = ReleaseInventoryCommand.of(item.productId(), item.quantity());
                releaseInventoryService.handle(command);
            }
            log.info("product.compensation.completed orderId={} itemCount={}",
                    compensationEvent.orderId(), compensationEvent.items().size());
        } catch (Exception e) {
            log.error("product.compensation.failed orderId={} message={}",
                    compensationEvent.orderId(), e.getMessage(), e);
        }
    }

    private boolean skipIfInitiatedByThisService(OrderCompensationEvent event) {
        if (groupId.equals(event.groupId())) {
            log.debug("product.compensation.ignored orderId={} reason=initiated_by_this_service",
                    event.orderId());
            return true;
        }
        return false;
    }
}
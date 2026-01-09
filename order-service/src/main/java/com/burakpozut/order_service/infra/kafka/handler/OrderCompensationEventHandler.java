package com.burakpozut.order_service.infra.kafka.handler;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.order_service.app.service.CancelOrderService;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompensationEventHandler {

    private final CancelOrderService cancelOrderService;

    public void handle(OrderCompensationEvent compensationEvent) {
        try {
            log.warn("Received compensation event for order: {}, Reason: {}",
                    compensationEvent.orderId(), compensationEvent.reason());
            cancelOrderService.handle(compensationEvent.orderId());
        } catch (Exception e) {
            log.error("Failed to process compensation event for order: {}, Error: {}",
                    compensationEvent.orderId(), e.getMessage());
            // TODO: We should warn the client about their order is undo
        }
    }

}

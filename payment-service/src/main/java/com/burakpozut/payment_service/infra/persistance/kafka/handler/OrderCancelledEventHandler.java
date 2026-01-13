package com.burakpozut.payment_service.infra.persistance.kafka.handler;

import com.burakpozut.common.event.order.OrderCancelledEvent;
import com.burakpozut.payment_service.app.service.CancelPaymentService;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledEventHandler {
    private final CancelPaymentService cancelPaymentService;

    public void handle(OrderCancelledEvent cancelledEvent) {
        log.info("Processing OrderCancelledEvent for order: {}, reason: {}",
                cancelledEvent.orderId(), cancelledEvent.reason());

        try {
            cancelPaymentService.handle(cancelledEvent.orderId());
            log.info("Successfully processed cancellation for order: {}", cancelledEvent.orderId());
        } catch (Exception e) {
            log.error("Failed to process cancellation for order: {}, Error: {}",
                    cancelledEvent.orderId(), e.getMessage(), e);
        }
    }

}

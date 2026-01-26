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
        log.info("payment.orderCancelled.received orderId={} reason={}",
                cancelledEvent.orderId(), cancelledEvent.reason());

        try {
            cancelPaymentService.handle(cancelledEvent.orderId());
            log.info("payment.orderCancelled.completed orderId={}", cancelledEvent.orderId());
        } catch (Exception e) {
            log.error("payment.orderCancelled.failed orderId={} message={}",
                    cancelledEvent.orderId(), e.getMessage(), e);
        }
    }

}

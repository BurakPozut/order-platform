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
            log.warn("order.compensation.received orderId={} reason={} initiatedBy={}",
                    compensationEvent.orderId(), compensationEvent.reason(), compensationEvent.groupId());
            cancelOrderService.handle(compensationEvent.orderId());
        } catch (Exception e) {
            log.error("order.compensation.failed orderId={} message={}",
                    compensationEvent.orderId(), e.getMessage(), e);
        }
    }

}

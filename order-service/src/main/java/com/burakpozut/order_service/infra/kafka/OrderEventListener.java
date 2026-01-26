package com.burakpozut.order_service.infra.kafka;

import com.burakpozut.common.event.order.OrderCancelledEvent;
import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.event.order.OrderEvent;
import com.burakpozut.order_service.infra.kafka.handler.OrderCompensationEventHandler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    private final OrderCompensationEventHandler orderCompensationEventHandler;

    @KafkaListener(topics = "${app.kafka.topics.order-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(@Payload OrderEvent event) {
        log.info("kafka.orderEvent.received orderId={} eventType={}",
                event.orderId(), event.getClass().getSimpleName());

        switch (event) {
            case OrderCompensationEvent compensationEvent ->
                orderCompensationEventHandler.handle(compensationEvent);

            case OrderCreatedEvent compensationEvent ->
                // Order service doesn't need to handle order created event;
                log.debug("kafka.orderEvent.ignored orderId={} eventType=OrderCreatedEvent reason=created_by_this_service",
                        compensationEvent.orderId());

            case OrderCancelledEvent cancelledEvent -> {
            }

        }
    }

}

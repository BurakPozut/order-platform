package com.burakpozut.payment_service.infra.persistance.kafka;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.event.order.OrderEvent;
import com.burakpozut.payment_service.infra.persistance.kafka.handler.OrderCompensationEventHandler;
import com.burakpozut.payment_service.infra.persistance.kafka.handler.OrderCreatedEventHandler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {
    private final OrderCreatedEventHandler orderCreatedEventHandler;
    private final OrderCompensationEventHandler orderCompensationEventHandler;

    @KafkaListener(topics = "${app.kafka.topics.order-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(@Payload OrderEvent event) {
        log.info("Received OrderEvent: type={}, orderId={}",
                event.getClass().getSimpleName(), event.orderId());

        switch (event) {
            case OrderCreatedEvent createdEvent -> orderCreatedEventHandler.handle(createdEvent);
            case OrderCompensationEvent compensationEvent -> orderCompensationEventHandler.handle(compensationEvent);
        }
    }

}

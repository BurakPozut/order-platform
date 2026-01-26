package com.burakpozut.order_service.infra.kafka;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.burakpozut.common.event.order.OrderCancelledEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.github.f4b6a3.uuid.UuidCreator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledPublisher {
    private final KafkaTemplate<String, OrderCancelledEvent> kafkaTemplate;

    @Value("${app.kafka.topics.order-events}")
    private String topic;

    public void publish(UUID orderId, UUID customerId, List<OrderItemEvent> items, String reason) {
        var event = OrderCancelledEvent.of(UuidCreator.getTimeOrdered(), Instant.now(), orderId, customerId, items,
                reason);

        kafkaTemplate.send(topic, orderId.toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("kafka.orderCancelled.published orderId={}", orderId);
                    } else {
                        log.error("kafka.orderCancelled.publish_failed orderId={} message={}",
                                orderId, exception.getMessage(), exception);
                    }
                });
    }

}

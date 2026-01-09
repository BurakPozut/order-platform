package com.burakpozut.notification_service.infra.kafka;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.github.f4b6a3.uuid.UuidCreator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompensationPublisher {

    private final KafkaTemplate<String, OrderCompensationEvent> kafkaTemplate;

    @Value("${app.kafka.topics.order-events}")
    private String topic;

    @Value("${spring.kafka.consumer.group-id}")
    private String serviceId;

    public void publish(UUID orderId, UUID customerId, List<OrderItemEvent> items, String reason) {
        var event = OrderCompensationEvent.of(
                UuidCreator.getTimeOrdered(),
                Instant.now(),
                orderId,
                customerId,
                items,
                reason,
                "notification-service");

        CompletableFuture<SendResult<String, OrderCompensationEvent>> future = kafkaTemplate.send(topic,
                orderId.toString(),
                event);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Successfully published OrderCompensationEvent for order: {} to partition: {}",
                        orderId, result.getRecordMetadata().partition());
            } else {
                log.error("Failed to publish OrderCompensationEvent for order: {}. Error: {}",
                        orderId, exception.getMessage(), exception);
                handlePublishFailure(orderId, exception);
            }
        });
    }

    private void handlePublishFailure(UUID orderId, Throwable failure) {
        log.warn("Event publish failed, Order might still be pending mode. OrderId: {}",
                orderId);
    }
}

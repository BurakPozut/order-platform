package com.burakpozut.product_service.infra.kafka;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.product_service.infra.kafka.failed_event.FailedEventJpaEntity;
import com.burakpozut.product_service.infra.kafka.failed_event.FailedEventStatus;
import com.burakpozut.product_service.infra.kafka.failed_event.SpringDataFailedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompensationPublisher {
    private final KafkaTemplate<String, OrderCompensationEvent> kafkaTemplate;
    private final SpringDataFailedEventRepository failedEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.order-events}")
    private String topic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    public void publish(UUID orderId, UUID customerId, List<OrderItemEvent> items, String reason) {
        var event = OrderCompensationEvent.of(UuidCreator.getTimeOrdered(),
                Instant.now(),
                orderId,
                customerId,
                items,
                reason,
                groupId);

        CompletableFuture<SendResult<String, OrderCompensationEvent>> future = kafkaTemplate.send(topic,
                orderId.toString(),
                event);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Successfully published OrderCompensationEvent for order: {} to partition: {}",
                        orderId, result.getRecordMetadata().partition());
            } else {
                log.error("Failed to publis OrderCompensationEvent for order: {}. Error: {}",
                        orderId, exception.getMessage(), exception);

                handlePublishFailure(event, exception);
            }
        });
    }

    @Transactional
    private void handlePublishFailure(OrderCompensationEvent event, Throwable failure) {
        try {
            String eventPayload = objectMapper.writeValueAsString(event);
            String errorMessage = failure.getMessage();
            if (errorMessage != null && errorMessage.length() > 1000) {
                errorMessage = errorMessage.substring(0, 1000);
            }

            var failedEvent = new FailedEventJpaEntity(
                    UuidCreator.getTimeOrdered(),
                    event.orderId(),
                    "ORDER",
                    event.getClass().getSimpleName(),
                    event.getClass().getName(),
                    eventPayload,
                    errorMessage,
                    FailedEventStatus.PENDING,
                    0,
                    null,
                    null,
                    null);

            failedEventRepository.save(failedEvent);
            log.info("Stored failed OrderCompensationEvent for order: {} in database for later retry",
                    event.orderId());
        } catch (Exception e) {
            log.error("Failed to store failed event for order: {}. Error: {}",
                    event.orderId(), e.getMessage(), e);
        }
    }
}

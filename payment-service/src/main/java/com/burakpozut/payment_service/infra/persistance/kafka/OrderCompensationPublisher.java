package com.burakpozut.payment_service.infra.persistance.kafka;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.payment_service.infra.persistance.failed_event.FailedEventJpaEntity;
import com.burakpozut.payment_service.infra.persistance.failed_event.FailedEventStatus;
import com.burakpozut.payment_service.infra.persistance.failed_event.SpringDataFailedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.MDC;
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
        var event = OrderCompensationEvent.of(
                UuidCreator.getTimeOrdered(),
                Instant.now(), orderId, customerId, items, reason, groupId);

        ProducerRecord<String, OrderCompensationEvent> record = new ProducerRecord<String, OrderCompensationEvent>(
                topic,
                orderId.toString(), event);
        String traceId = MDC.get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            record.headers().add(new RecordHeader("X-Trace-Id", traceId.getBytes()));
        }

        CompletableFuture<SendResult<String, OrderCompensationEvent>> future = kafkaTemplate.send(record);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("kafka.orderCompensation.published orderId={} partition={}",
                        orderId, result.getRecordMetadata().partition());
            } else {
                log.error("kafka.orderCompensation.publish_failed orderId={} message={}",
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
            log.info("kafka.orderCompensation.failed_event_stored orderId={} action=retry_later",
                    event.orderId());
        } catch (Exception e) {
            log.error("kafka.orderCompensation.failed_event_storage_failed orderId={} message={}",
                    event.orderId(), e.getMessage(), e);
        }
    }
}

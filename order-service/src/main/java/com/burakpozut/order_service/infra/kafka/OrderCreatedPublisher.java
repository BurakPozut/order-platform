package com.burakpozut.order_service.infra.kafka;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.infra.persistance.failed_event.FailedEventJpaEntity;
import com.burakpozut.order_service.infra.persistance.failed_event.FailedEventStatus;
import com.burakpozut.order_service.infra.persistance.failed_event.SpringDataFailedEventRepository;
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
public class OrderCreatedPublisher {

    // In product command for inventroy checking we need
    // - productId, - quantity
    // In notification service create notification command we dont need anything
    // extrea other than Order domain object
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final SpringDataFailedEventRepository failedEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.order-events}")
    private String topic;

    public void publish(Order order) {
        List<OrderItemEvent> items = order.items().stream()
                .map(item -> OrderItemEvent.of(
                        item.productId(),
                        item.quantity()))
                .toList();

        var event = OrderCreatedEvent.of(
                UuidCreator.getTimeOrdered(),
                Instant.now(),
                order.id(),
                order.customerId(),
                order.totalAmount(),
                order.currency(),
                items);

        ProducerRecord<String, OrderCreatedEvent> record = new ProducerRecord<String, OrderCreatedEvent>(topic,
                order.id().toString(), event);
        String traceId = MDC.get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            record.headers().add(new RecordHeader("X-Trace-Id", traceId.getBytes()));
        }

        CompletableFuture<SendResult<String, OrderCreatedEvent>> future = kafkaTemplate.send(record);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("kafka.orderCreated.published orderId={} partition={}",
                        order.id(),
                        result.getRecordMetadata().partition());
            } else {
                log.error("kafka.orderCreated.publish_failed orderId={} message={}",
                        order.id(), exception.getMessage(), exception);
                handlePublishFailure(event, exception);
            }
        });

    }

    @Transactional
    private void handlePublishFailure(OrderCreatedEvent event, Throwable failure) {
        // For notifications (non-critical), just log
        // For critical events, you might want to:
        // 1. Store in database for later retry
        // 2. Retry with exponential backoff
        // 3. Throw exception to fail the operation

        try {
            String eventPaylod = objectMapper.writeValueAsString(event);
            String errorMessage = failure.getMessage();
            if (errorMessage != null && errorMessage.length() > 1000) {
                errorMessage = errorMessage.substring(0, 1000);
            }

            var failedEvent = new FailedEventJpaEntity(
                    UuidCreator.getTimeOrdered(),
                    event.orderId(),
                    "ORDER",
                    event.getClass().getSimpleName(),
                    eventPaylod.getClass().getName(),
                    eventPaylod,
                    errorMessage,
                    FailedEventStatus.PENDING,
                    0,
                    null,
                    null,
                    null);

            failedEventRepository.save(failedEvent);
            log.info("kafka.orderCreated.failed_event_stored orderId={} action=retry_later", event.orderId());
        } catch (Exception e) {
            log.error("kafka.orderCreated.failed_event_storage_failed orderId={} message={}",
                    event.orderId(), e.getMessage(), e);
        }
    }
}

package com.burakpozut.order_service.infra.kafka;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.event.order.OrderEvent;
import com.burakpozut.order_service.app.service.CancelOrderService;
import com.burakpozut.order_service.infra.persistance.failed_event.FailedEventJpaEntity;
import com.burakpozut.order_service.infra.persistance.failed_event.FailedEventStatus;
import com.burakpozut.order_service.infra.persistance.failed_event.SpringDataFailedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FailedEventRetryService {

    private static final int MAX_RETRIES = 5;

    private final CancelOrderService cancelOrderService;
    private final SpringDataFailedEventRepository failedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final Map<String, Class<? extends OrderEvent>> EVENT_CLASS_REGISTRY = Map.of(
            "OrderCreatedEvent", OrderCreatedEvent.class,
            "OrderCompensationEvent", OrderCompensationEvent.class);

    @Value("${app.kafka.topics.order-events}")
    private String topic;

    @Scheduled(fixedDelayString = "${app.failed-events.retry-intervals:60000}")
    @Transactional
    public void retryFailedEvents() {
        List<FailedEventJpaEntity> pendingEvents = failedEventRepository
                .findByStatusOrderByCreatedAtAsc(FailedEventStatus.PENDING);
        List<FailedEventJpaEntity> processingEvents = failedEventRepository
                .findByStatusOrderByCreatedAtAsc(FailedEventStatus.PROCESSING);

        List<FailedEventJpaEntity> failedEvents = new ArrayList<>();
        failedEvents.addAll(pendingEvents);
        failedEvents.addAll(processingEvents);

        // Filter by max retries
        List<FailedEventJpaEntity> eventsToRetry = failedEvents.stream()
                .filter(event -> event.getRetryCount() < MAX_RETRIES).toList();

        if (eventsToRetry.isEmpty()) {
            return;
        }

        log.info("Found {} failed events to retry", eventsToRetry.size());

        for (FailedEventJpaEntity failedEvent : eventsToRetry) {
            try {
                retryEvent(failedEvent);
            } catch (Exception e) {
                log.error("Error retrying failed event: {}. Error: {}",
                        failedEvent.getId(), e.getMessage());
            }
        }

    }

    private void retryEvent(FailedEventJpaEntity failedEvent) {
        try {
            Class<? extends OrderEvent> eventClass = getEventClass(failedEvent.getEventType());
            if (eventClass == null) {
                markAsFailed(failedEvent, "Unknown event type: " + failedEvent.getEventType());
                return;
            }

            OrderEvent event = objectMapper.readValue(
                    failedEvent.getEventPayload(), eventClass);

            String kafkaKey = extractKafkaKey(failedEvent, event);

            failedEvent.setStatus(FailedEventStatus.PROCESSING);
            failedEvent.setRetryCount(failedEvent.getRetryCount() + 1);
            failedEvent.setLastRetryAt(LocalDateTime.now());
            failedEventRepository.save(failedEvent);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    topic, kafkaKey, event);

            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("Successfully retried event: {} for entity: {} (attempt {}/{})",
                            failedEvent.getId(), failedEvent.getEntityId(),
                            failedEvent.getRetryCount(), MAX_RETRIES);
                    markAsCompleted(failedEvent);
                } else {
                    log.warn("Retry failed for event: {} (attempt {}/{})",
                            failedEvent.getId(), failedEvent.getRetryCount(), MAX_RETRIES);
                    handleRetryFailure(failedEvent, exception);
                }
            });
        } catch (Exception e) {
            log.error("Error deserializing or retrying event: {}. Error: {}",
                    failedEvent.getId(), e.getMessage(), e);
            markAsFailed(failedEvent, "Deserialization error: " + e.getMessage());
        }

    }

    private Class<? extends OrderEvent> getEventClass(String eventType) {
        return switch (eventType) {
            case "OrderCreatedEvent" -> OrderCreatedEvent.class;
            case "OrderCompensationEvent" -> OrderCompensationEvent.class;

            default -> {
                Class<? extends OrderEvent> clazz = EVENT_CLASS_REGISTRY.get(eventType);
                log.error("Could find the event type in the FailedEventRetryService.");
                yield clazz;
            }
        };
    }

    @Transactional
    private String extractKafkaKey(FailedEventJpaEntity failedEvent, OrderEvent event) {
        if (failedEvent.getEntityId() != null) {
            return failedEvent.getEntityId().toString();
        }
        return event.orderId().toString();
    }

    @Transactional
    private void handleRetryFailure(FailedEventJpaEntity failedEvent, Throwable exception) {
        if (failedEvent.getRetryCount() >= MAX_RETRIES) {
            markAsFailed(failedEvent, "Max retries exceeded: " + exception.getMessage());
        } else {
            failedEvent.setStatus(FailedEventStatus.PENDING);
            String errorMessage = exception.getMessage();
            if (errorMessage != null && errorMessage.length() > 1000) {
                errorMessage = errorMessage.substring(0, 1000);
            }
            failedEvent.setErrorMessage(errorMessage);
            failedEventRepository.save(failedEvent);
        }
    }

    @Transactional
    private void markAsCompleted(FailedEventJpaEntity failedEvent) {
        failedEvent.setStatus(FailedEventStatus.COMPLETED);
        failedEventRepository.save(failedEvent);
        log.info("Marked failed event: {} as completed", failedEvent.getId());
    }

    @Transactional
    private void markAsFailed(FailedEventJpaEntity failedEvent, String errorMessage) {
        failedEvent.setStatus(FailedEventStatus.FAILED);
        if (errorMessage != null && errorMessage.length() > 1000) {
            errorMessage = errorMessage.substring(0, 1000);
        }
        failedEvent.setErrorMessage(errorMessage);
        failedEventRepository.save(failedEvent);

        // We need to check if it is a order event before we cancel it
        if ("OrderCreatedEvent".equals(failedEvent.getEventType()) &&
                "Order".equals(failedEvent.getEntityType())) {

            UUID orderId = failedEvent.getEntityId();
            if (orderId == null)
                log.warn("Cannot cancel order: entityId is null for failed event: {}",
                        failedEvent.getId());

            try {
                cancelOrderService.handle(failedEvent.getEntityId());
                log.info("Cancelled order: {} due to failed OrderCreatedEvent publish", orderId);
            } catch (Exception e) {
                log.error("Failed to cancel order: {} for failed event: {}. Error: {}",
                        orderId, failedEvent.getId(), e.getMessage(), e);
            }

        }
        log.warn("Marked failed event: {} as permanently failed after {} retries",
                failedEvent.getId(), failedEvent.getRetryCount());
    }
}

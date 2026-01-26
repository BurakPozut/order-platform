package com.burakpozut.product_service.infra.kafka.failed_event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.burakpozut.common.event.order.OrderEvent;
import com.burakpozut.common.event.order.ServiceCompletionEvent;
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

    private final SpringDataFailedEventRepository failedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final Map<String, Class<?>> EVENT_CLASS_REGISTRY = Map.of(
            "OrderCompensationEvent", OrderCompensationEvent.class,
            "ServiceCompletionEvent", ServiceCompletionEvent.class);

    @Value("${app.kafka.topics.order-events}")
    private String orderEventsTopic;

    @Value("${app.kafka.topics.service-completions}")
    private String serviceCompletionsTopic;

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

        log.info("failedEvent.retry.start count={}", eventsToRetry.size());

        for (FailedEventJpaEntity failedEvent : eventsToRetry) {
            try {
                retryEvent(failedEvent);
            } catch (Exception e) {
                log.error("failedEvent.retry.error eventId={} message={}",
                        failedEvent.getId(), e.getMessage(), e);
            }
        }
    }

    private void retryEvent(FailedEventJpaEntity failedEvent) {
        try {
            Class<?> eventClass = getEventClass(failedEvent.getEventType());
            if (eventClass == null) {
                markAsFailed(failedEvent, "Unknown event type: " + failedEvent.getEventType());
                return;
            }

            Object event = objectMapper.readValue(failedEvent.getEventPayload(), eventClass);
            String topic = determineTopic(failedEvent.getEventType());
            String kafkaKey = extractKafkaKey(failedEvent, event);

            failedEvent.setStatus(FailedEventStatus.PROCESSING);
            failedEvent.setRetryCount(failedEvent.getRetryCount() + 1);
            failedEvent.setLastRetryAt(LocalDateTime.now());
            failedEventRepository.save(failedEvent);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    topic, kafkaKey, event);

            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("failedEvent.retry.success eventId={} entityId={} attempt={} maxRetries={}",
                            failedEvent.getId(), failedEvent.getEntityId(),
                            failedEvent.getRetryCount(), MAX_RETRIES);
                    markAsCompleted(failedEvent);
                } else {
                    log.warn("failedEvent.retry.failed eventId={} attempt={} maxRetries={}",
                            failedEvent.getId(), failedEvent.getRetryCount(), MAX_RETRIES);
                    handleRetryFailure(failedEvent, exception);
                }
            });
        } catch (Exception e) {
            log.error("failedEvent.retry.deserialization_error eventId={} message={}",
                    failedEvent.getId(), e.getMessage(), e);
            markAsFailed(failedEvent, "Deserialization error: " + e.getMessage());
        }
    }

    private Class<?> getEventClass(String eventType) {
        Class<?> clazz = EVENT_CLASS_REGISTRY.get(eventType);
        if (clazz == null) {
            log.error("failedEvent.retry.unknown_event_type eventType={}", eventType);
        }
        return clazz;
    }

    private String determineTopic(String eventType) {
        return switch (eventType) {
            case "OrderCompensationEvent" -> orderEventsTopic;
            case "ServiceCompletionEvent" -> serviceCompletionsTopic;
            default -> {
                log.warn("failedEvent.retry.unknown_topic eventType={} action=using_order_events", eventType);
                yield orderEventsTopic;
            }
        };
    }

    @Transactional
    private String extractKafkaKey(FailedEventJpaEntity failedEvent, Object event) {
        if (failedEvent.getEntityId() != null) {
            return failedEvent.getEntityId().toString();
        }
        // Extract orderId from event
        if (event instanceof OrderEvent orderEvent) {
            return orderEvent.orderId().toString();
        } else if (event instanceof ServiceCompletionEvent serviceEvent) {
            return serviceEvent.orderId().toString();
        }
        return failedEvent.getEntityId() != null ? failedEvent.getEntityId().toString() : null;
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
        log.info("failedEvent.completed eventId={}", failedEvent.getId());
    }

    @Transactional
    private void markAsFailed(FailedEventJpaEntity failedEvent, String errorMessage) {
        failedEvent.setStatus(FailedEventStatus.FAILED);
        if (errorMessage != null && errorMessage.length() > 1000) {
            errorMessage = errorMessage.substring(0, 1000);
        }
        failedEvent.setErrorMessage(errorMessage);
        failedEventRepository.save(failedEvent);
        log.warn("failedEvent.permanently_failed eventId={} retryCount={}",
                failedEvent.getId(), failedEvent.getRetryCount());
    }
}
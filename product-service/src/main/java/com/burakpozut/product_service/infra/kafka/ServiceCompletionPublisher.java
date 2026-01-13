package com.burakpozut.product_service.infra.kafka;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.domain.ServiceName;
import com.burakpozut.common.event.order.ServiceCompletionEvent;
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
public class ServiceCompletionPublisher {

    private final KafkaTemplate<String, ServiceCompletionEvent> kafkaTemplate;
    private final SpringDataFailedEventRepository failedEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.service-completions}")
    private String topic;

    public void publish(UUID orderId, ServiceName serviceName) {
        var event = ServiceCompletionEvent.of(orderId, serviceName);

        CompletableFuture<SendResult<String, ServiceCompletionEvent>> future = kafkaTemplate.send(
                topic, orderId.toString(), event);

        future.whenComplete((resutl, exception) -> {
            if (exception == null) {
                log.info(
                        "Successfully published ServiceCompletionEvent for order: {} form service: {} to partition: {}",
                        orderId, serviceName, (resutl.getRecordMetadata()));
            } else {
                log.error("Failed to publish ServiceCompletionEvent for order: {} from service: {}, Error: {}",
                        orderId, serviceName, exception.getMessage(), exception);
                handlePublishFailure(event, exception);
            }
        });
    }

    @Transactional
    private void handlePublishFailure(ServiceCompletionEvent event, Throwable failure) {
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
            log.info("Stored failed ServiceCompletionEvent for order: {} in database for later retry",
                    event.orderId());
        } catch (Exception e) {
            log.error("Failed to store failed event for order: {}. Error: {}",
                    event.orderId(), e.getMessage(), e);
        }
    }

}

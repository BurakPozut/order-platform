package com.burakpozut.payment_service.infra.persistance.kafka;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.domain.ServiceName;
import com.burakpozut.common.event.order.ServiceCompletionEvent;
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
public class ServiceCompletionPublisher {

    private final KafkaTemplate<String, ServiceCompletionEvent> kafkaTemplate;
    private final SpringDataFailedEventRepository failedEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.service-completions}")
    private String topic;

    public void publish(UUID orderId, ServiceName serviceName) {
        var event = ServiceCompletionEvent.of(orderId, serviceName);

        ProducerRecord<String, ServiceCompletionEvent> record = new ProducerRecord<String, ServiceCompletionEvent>(
                topic,
                orderId.toString(), event);
        String traceId = MDC.get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            record.headers().add(new RecordHeader("X-Trace-Id", traceId.getBytes()));
        }

        CompletableFuture<SendResult<String, ServiceCompletionEvent>> future = kafkaTemplate.send(topic,
                orderId.toString(), event);

        future.whenComplete((result, exception) -> {
            if (traceId != null && !traceId.isBlank()) {
                MDC.put("traceId", traceId);
            }

            try {

                if (exception == null) {
                    log.info("kafka.serviceCompletion.published orderId={} serviceName={} partition={}",
                            orderId, serviceName, result.getRecordMetadata().partition());
                } else {
                    log.error("kafka.serviceCompletion.publish_failed orderId={} serviceName={} message={}",
                            orderId, serviceName, exception.getMessage(), exception);
                    handlePublishFailure(event, exception);
                }

            } finally {
                MDC.remove("traceId");
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
            log.info("kafka.serviceCompletion.failed_event_stored orderId={} action=retry_later",
                    event.orderId());
        } catch (Exception e) {
            log.error("kafka.serviceCompletion.failed_event_storage_failed orderId={} message={}",
                    event.orderId(), e.getMessage(), e);
        }
    }
}

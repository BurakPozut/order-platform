package com.burakpozut.notification_service.infra.kafka;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.domain.ServiceName;
import com.burakpozut.common.event.order.ServiceCompletionEvent;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceCompletionPublisher {

    private final KafkaTemplate<String, ServiceCompletionEvent> kafkaTemplate;

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
        CompletableFuture<SendResult<String, ServiceCompletionEvent>> future = kafkaTemplate.send(record);

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
                    handlePublishFailure(orderId, serviceName, exception);
                }

            } finally {
                MDC.remove("traceId");
            }
        });
    }

    private void handlePublishFailure(UUID orderId, ServiceName serviceName, Throwable failure) {
        log.warn(
                "kafka.serviceCompletion.publish_failed orderId={} serviceName={} message={} action=order_confirmation_delayed",
                orderId, serviceName, failure.getMessage());
    }

}

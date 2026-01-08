package com.burakpozut.notification_service.infra.kafka;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.domain.ServiceName;
import com.burakpozut.common.event.order.ServiceCompletionEvent;

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

        CompletableFuture<SendResult<String, ServiceCompletionEvent>> future = kafkaTemplate.send(topic,
                orderId.toString(), event);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("Successfully publish ServiceCompletionEvent for order: {} from service: {} to partition: {}",
                        orderId, serviceName, result.getRecordMetadata().partition());
            } else {
                log.error("Failed to publish ServiceCompletionEvent for order:{} from service: {}, Error: {}",
                        orderId, serviceName, exception.getMessage(), exception);
                handlePublishFailure(orderId, serviceName, exception);
            }
        });
    }

    private void handlePublishFailure(UUID orderId, ServiceName serviceName, Throwable failure) {
        log.warn("ServiceCompletionEvent publish failed for order: {} from service: {}. " +
                "Order confirmation may be delayed.", orderId, serviceName);
    }

}

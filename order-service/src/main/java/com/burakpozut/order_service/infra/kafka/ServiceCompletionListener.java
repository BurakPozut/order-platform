package com.burakpozut.order_service.infra.kafka;

import com.burakpozut.common.event.order.ServiceCompletionEvent;
import com.burakpozut.order_service.infra.kafka.handler.ServiceCompletionEventHandler;

import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceCompletionListener {
    private final ServiceCompletionEventHandler serviceCompletionEventHandler;

    @KafkaListener(topics = "${app.kafka.topics.service-completions}", groupId = "${spring.kafka.consumer.group-id.service-completions}")
    public void onMessage(@Payload ServiceCompletionEvent event,
            @Header(name = "X-Trace-Id") String traceId) {

        if (traceId != null && !traceId.isBlank()) {
            MDC.put("traceId", traceId);
        }
        try {

            log.info("kafka.serviceCompletion.received orderId={} serviceName={}",
                    event.orderId(), event.serviceName());

            serviceCompletionEventHandler.handle(event);

        } finally {
            MDC.remove("traceId");
        }
    }

}

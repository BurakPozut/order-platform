package com.burakpozut.notification_service.infra.kafka;

import java.time.Instant;
import java.util.UUID;

import com.burakpozut.common.event.order.OrderCompensationEvent;
import com.github.f4b6a3.uuid.UuidCreator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCompensationPublisher {
  private final KafkaTemplate<String, OrderCompensationEvent> kafkaTemplate;

  @Value("${app.kafka.topics.order-events}")
  private String topic;

  public void publish(UUID orderId, UUID customerId, String reason) {
    var event = new OrderCompensationEvent(
        UuidCreator.getTimeOrdered(),
        Instant.now(),
        orderId,
        customerId,
        reason);

    kafkaTemplate.send(topic, orderId.toString(), event);
  }

}

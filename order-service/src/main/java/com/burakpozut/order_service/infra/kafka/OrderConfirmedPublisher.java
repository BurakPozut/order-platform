package com.burakpozut.order_service.infra.kafka;

import java.time.Instant;
import java.util.UUID;

import com.burakpozut.common.event.OrderConfirmedEvent;
import com.burakpozut.order_service.domain.Order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderConfirmedPublisher {
  private final KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate;

  @Value("${app.kafka.topics.order-confirmed}")
  private String topic;

  public void publish(Order order) {
    var event = new OrderConfirmedEvent(
        UUID.randomUUID(),
        Instant.now(),
        order.id(),
        order.customerId());

    kafkaTemplate.send(topic, order.id().toString(), event);
  }

}

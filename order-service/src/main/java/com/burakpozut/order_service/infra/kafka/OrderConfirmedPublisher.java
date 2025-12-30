package com.burakpozut.order_service.infra.kafka;

import java.time.Instant;

import com.burakpozut.common.event.order.OrderConfirmedEvent;
import com.burakpozut.order_service.domain.Order;
import com.github.f4b6a3.uuid.UuidCreator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderConfirmedPublisher {
  private final KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate;

  @Value("${app.kafka.topics.order-events}")
  private String topic;

  public void publish(Order order) {
    var event = new OrderConfirmedEvent(
        UuidCreator.getTimeOrdered(),
        Instant.now(),
        order.id(),
        order.customerId());

    kafkaTemplate.send(topic, order.id().toString(), event);
  }

  // TODO: Do we need resilience here? Maybe notification service is down or fed
  // up?
}

package com.burakpozut.order_service.infra.kafka;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.order_service.domain.Order;
import com.github.f4b6a3.uuid.UuidCreator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedPublisher {

  // In product command for inventroy checking we need
  // - productId, - quantity
  // In notification service create notification command we dont need anything
  // extrea other than Order domain object
  private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

  @Value("${app.kafka.topics.order-events}")
  private String topic;

  public void publish(Order order) {
    List<OrderItemEvent> items = order.items().stream()
        .map(item -> OrderItemEvent.of(
            item.productId(),
            item.quantity()))
        .toList();

    var event = OrderCreatedEvent.of(
        UuidCreator.getTimeOrdered(),
        Instant.now(),
        order.id(),
        order.customerId(),
        order.totalAmount(),
        order.currency(),
        items);

    CompletableFuture<SendResult<String, OrderCreatedEvent>> future = kafkaTemplate.send(topic,
        order.id().toString(),
        event);

    future.whenComplete((result, exception) -> {
      if (exception == null) {
        log.info("Successfully published OrderConfirmedEvent for order: {} to partition: {}",
            order.id(),
            result.getRecordMetadata().partition());
      } else {
        log.error("Failed to publish OrderConfirmedEvent for order: {}. Error: {}",
            order.id(), exception.getMessage(), exception);
        handlePublishFailure(event, exception);
      }
    });

  }

  private void handlePublishFailure(OrderCreatedEvent event, Throwable failure) {
    // For notifications (non-critical), just log
    // For critical events, you might want to:
    // 1. Store in database for later retry
    // 2. Retry with exponential backoff
    // 3. Throw exception to fail the operation

    log.warn("Event publish failed but order continues. Order: {}. " +
        "Notification may not be sent, but order is still valid.", event.orderId());
    // Optionally: Store failed event for later retry
    // failedEventRepository.save(new FailedEvent(event, failure));
  }

  // TODO: Do we need resilience here? Maybe notification service is down or fed
  // up?
}

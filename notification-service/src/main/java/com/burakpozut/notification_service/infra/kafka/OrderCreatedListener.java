package com.burakpozut.notification_service.infra.kafka;

import com.burakpozut.common.event.order.OrderCreatedEvent;
import com.burakpozut.common.event.order.OrderEvent;
import com.burakpozut.notification_service.app.command.CreateNotificationCommand;
import com.burakpozut.notification_service.app.service.CreateNotificationService;
import com.burakpozut.notification_service.domain.NotificationChannel;
import com.burakpozut.notification_service.domain.NotificationStatus;
import com.burakpozut.notification_service.domain.NotificationType;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedListener {

  private final CreateNotificationService createNotificationService;
  private final OrderCompensationPublisher compensationPublisher;

  @KafkaListener(topics = "${app.kafka.topics.order-events}", groupId = "${spring.kafka.consumer.group-id}")
  public void onMessage(@Payload OrderEvent event) {
    if (event instanceof OrderCreatedEvent createdEvent) {

      try {
        var command = CreateNotificationCommand.of(
            createdEvent.customerId(), createdEvent.orderId(),
            NotificationType.ORDER_CONFIRMED,
            NotificationChannel.EMAIL,
            NotificationStatus.PENDING);

        createNotificationService.handle(command);
        log.info("Successfully created notification for order: {}", createdEvent.orderId());
      } catch (Exception e) {
        log.error("Failed to create notification for order: {}, Reason: {}",
            createdEvent.orderId(), e.getMessage(), e);

        compensationPublisher.publish(
            createdEvent.orderId(),
            createdEvent.customerId(),
            createdEvent.items(),
            "Notification service failed: " + e.getMessage());
      }
    } // we are ignoring OrderCompensationEvent right now
  }

}

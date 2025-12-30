package com.burakpozut.notification_service.infra.kafka;

import com.burakpozut.common.event.order.OrderConfirmedEvent;
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
public class OrderConfirmedListener {

  private final CreateNotificationService createNotificationService;
  private final OrderCompensationPublisher compensationPublisher;

  @KafkaListener(topics = "${app.kafka.topics.order-events}", groupId = "notification-service")
  public void onMessage(@Payload OrderEvent event) {
    if (event instanceof OrderConfirmedEvent) {

      try {
        var command = CreateNotificationCommand.of(
            event.customerId(), event.orderId(),
            NotificationType.ORDER_CONFIRMED,
            NotificationChannel.EMAIL,
            NotificationStatus.PENDING);

        createNotificationService.handle(command);
        log.info("Successfully created notification for order: {}", event.orderId());
      } catch (Exception e) {
        log.error("Failed to create notification for order: {}, Reason: {}",
            event.orderId(), e.getMessage(), e);

        compensationPublisher.publish(
            event.orderId(),
            event.customerId(),
            "Notification service failed: " + e.getMessage());
      }
    } // we are ignoring OrderCompensationEvent right now
  }

}

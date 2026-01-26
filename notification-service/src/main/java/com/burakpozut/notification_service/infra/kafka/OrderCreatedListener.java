package com.burakpozut.notification_service.infra.kafka;

import com.burakpozut.common.domain.ServiceName;
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
    private final ServiceCompletionPublisher serviceCompletionPublisher;

    @KafkaListener(topics = "${app.kafka.topics.order-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(@Payload OrderEvent event) {
        if (event instanceof OrderCreatedEvent createdEvent) {

            try {
                var command = CreateNotificationCommand.of(
                        createdEvent.customerId(), createdEvent.orderId(),
                        NotificationType.ORDER_CONFIRMED,
                        NotificationChannel.EMAIL,
                        NotificationStatus.PENDING);

                var notification = createNotificationService.handle(command);
                log.info("notification.orderCreated.notification_created notificationId={} orderId={} customerId={}",
                        notification.id(), createdEvent.orderId(), createdEvent.customerId());

                serviceCompletionPublisher.publish(createdEvent.orderId(), ServiceName.NOTIFICATION);
                log.debug("notification.orderCreated.completion_published orderId={} serviceName=NOTIFICATION",
                        createdEvent.orderId());
            } catch (Exception e) {
                log.error("notification.orderCreated.failed orderId={} customerId={} message={} action=compensate",
                        createdEvent.orderId(), createdEvent.customerId(), e.getMessage(), e);

                compensationPublisher.publish(
                        createdEvent.orderId(),
                        createdEvent.customerId(),
                        createdEvent.items(),
                        "Notification service failed: " + e.getMessage());
            }
        } // we are ignoring OrderCompensationEvent right now
    }

}

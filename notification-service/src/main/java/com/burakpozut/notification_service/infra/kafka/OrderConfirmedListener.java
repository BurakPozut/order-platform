package com.burakpozut.notification_service.infra.kafka;

import com.burakpozut.common.event.OrderConfirmedEvent;
import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationChannel;
import com.burakpozut.notification_service.domain.NotificationRepository;
import com.burakpozut.notification_service.domain.NotificationStatus;
import com.burakpozut.notification_service.domain.NotificationType;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderConfirmedListener {

  private final NotificationRepository notificationRepository;

  @KafkaListener(topics = "${app.kafka.topics.order-confirmed}")
  public void onMessage(OrderConfirmedEvent event) {
    var notification = Notification.of(
        event.customerId(), event.orderId(),
        NotificationType.ORDER_CONFIRMED,
        NotificationChannel.EMAIL,
        NotificationStatus.PENDING);

    notificationRepository.save(notification);
  }

}

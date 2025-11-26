package com.burakpozut.microservices.order_platform.payment.infrastructure.notification;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.notification.domain.Notification;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationRepository;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationStatus;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationType;
import com.burakpozut.microservices.order_platform.payment.domain.port.NotificationGateway;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationGatewayAdapter implements NotificationGateway {
  private final NotificationRepository notificationRepository;

  @Override
  public void createNotificationForPayment(UUID customerId, UUID orderId, String type, String channel, String status) {
    var notification = Notification.createNew(customerId, orderId, NotificationType.valueOf(type), channel,
        NotificationStatus.valueOf(status));
    notificationRepository.save(notification);
  }
}

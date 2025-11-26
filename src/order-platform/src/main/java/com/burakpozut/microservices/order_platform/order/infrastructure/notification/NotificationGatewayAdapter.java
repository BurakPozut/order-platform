package com.burakpozut.microservices.order_platform.order.infrastructure.notification;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.notification.domain.Notification;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationRepository;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationStatus;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationType;
import com.burakpozut.microservices.order_platform.order.domain.port.NotificationGateway;

import lombok.RequiredArgsConstructor;

@Repository("orderNotificationGatewayAdapter")
@RequiredArgsConstructor
public class NotificationGatewayAdapter implements NotificationGateway {
  private final NotificationRepository notificationRepository;

  @Override
  public void createNotificationForOrder(UUID customerId, UUID orderId, String type, String channel, String status) {
    var notification = Notification.createNew(customerId, orderId, NotificationType.valueOf(type), channel,
        NotificationStatus.valueOf(status));
    notificationRepository.save(notification);
  }
}

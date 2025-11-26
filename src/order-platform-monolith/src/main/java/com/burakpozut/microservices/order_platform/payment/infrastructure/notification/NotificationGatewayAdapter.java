package com.burakpozut.microservices.order_platform.payment.infrastructure.notification;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.notification.domain.NotificationRepository;
import com.burakpozut.microservices.order_platform.payment.domain.port.NotificationGateway;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationGatewayAdapter implements NotificationGateway {
  private final NotificationRepository notificationRepository;

  @Override
  public void createNotificationForPayment(UUID customerId, UUID orderId, String type, String channel, String status) {
    notificationRepository.save(null);
  }
}

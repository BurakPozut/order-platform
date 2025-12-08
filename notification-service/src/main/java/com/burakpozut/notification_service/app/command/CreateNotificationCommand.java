package com.burakpozut.notification_service.app.command;

import java.util.UUID;

import com.burakpozut.notification_service.domain.NotificationChannel;
import com.burakpozut.notification_service.domain.NotificationStatus;
import com.burakpozut.notification_service.domain.NotificationType;

public record CreateNotificationCommand(
    UUID customerId,
    UUID orderId,
    NotificationType type,
    NotificationChannel channel,
    NotificationStatus status) {
  public static CreateNotificationCommand of(UUID customerId, UUID orderId,
      NotificationType type, NotificationChannel channel,
      NotificationStatus status) {
    return new CreateNotificationCommand(customerId, orderId, type, channel, status);
  }

}

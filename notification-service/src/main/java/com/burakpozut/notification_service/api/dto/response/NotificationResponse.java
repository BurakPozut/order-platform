package com.burakpozut.notification_service.api.dto.response;

import java.util.UUID;

import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationChannel;
import com.burakpozut.notification_service.domain.NotificationStatus;
import com.burakpozut.notification_service.domain.NotificationType;

public record NotificationResponse(
    UUID id,
    UUID customerId,
    UUID orderId,
    NotificationType type,
    NotificationChannel channel,
    NotificationStatus status

) {
  public static NotificationResponse from(Notification n) {
    return new NotificationResponse(n.id(), n.customerId(), n.orderId(), n.type(), n.channel(), n.status());
  }

}

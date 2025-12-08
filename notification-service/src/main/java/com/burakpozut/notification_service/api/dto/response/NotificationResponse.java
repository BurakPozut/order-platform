package com.burakpozut.notification_service.api.dto.response;

import java.util.UUID;

import com.burakpozut.notification_service.domain.Notification;

public record NotificationResponse(
    UUID id,
    UUID customerId,
    UUID orderId,
    String type,
    String channel,
    String status

) {
  public static NotificationResponse from(Notification n) {
    return new NotificationResponse(n.id(), n.customerId(), n.orderId(), n.type(), n.channel(), n.status());
  }

}

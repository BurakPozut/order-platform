package com.burakpozut.microservices.order_platform.notification.api.dto;

import com.burakpozut.microservices.order_platform.notification.domain.Notification;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationType;

public record NotificationResponse(
    NotificationType type,
    String channed) {

  public static NotificationResponse from(Notification notification) {
    return new NotificationResponse(notification.getType(), notification.getChanned());
  }
}

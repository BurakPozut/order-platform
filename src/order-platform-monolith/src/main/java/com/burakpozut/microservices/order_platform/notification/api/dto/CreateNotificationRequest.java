package com.burakpozut.microservices.order_platform.notification.api.dto;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.notification.domain.NotificationStatus;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationType;

public record CreateNotificationRequest(
    UUID customerId,
    UUID orderId,
    NotificationType type,
    String channel,
    NotificationStatus status) {

}

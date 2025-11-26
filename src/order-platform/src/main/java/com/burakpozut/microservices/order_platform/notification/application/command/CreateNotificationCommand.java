package com.burakpozut.microservices.order_platform.notification.application.command;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.notification.domain.NotificationStatus;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationType;

import jakarta.validation.constraints.NotNull;

public record CreateNotificationCommand(
    @NotNull UUID customerId,
    @NotNull UUID orderId,
    NotificationType type,
    String channel,
    NotificationStatus status) {

}

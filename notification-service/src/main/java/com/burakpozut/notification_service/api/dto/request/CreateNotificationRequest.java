package com.burakpozut.notification_service.api.dto.request;

import java.util.UUID;

import com.burakpozut.notification_service.domain.NotificationChannel;
import com.burakpozut.notification_service.domain.NotificationStatus;
import com.burakpozut.notification_service.domain.NotificationType;

import jakarta.validation.constraints.NotNull;

public record CreateNotificationRequest(
    @NotNull UUID customerId,
    @NotNull UUID orderId,
    @NotNull NotificationType type,
    @NotNull NotificationChannel channel,
    @NotNull NotificationStatus status) {

}

package com.burakpozut.notification_service.api;

import com.burakpozut.notification_service.api.dto.request.CreateNotificationRequest;
import com.burakpozut.notification_service.app.command.CreateNotificationCommand;

public class NotificationMapper {
  public static CreateNotificationCommand toCommand(CreateNotificationRequest request) {
    return CreateNotificationCommand.of(request.customerId(), request.orderId(),
        request.type(), request.channel(), request.status());
  }

}

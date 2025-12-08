package com.burakpozut.notification_service.app.service;

import org.springframework.stereotype.Service;

import com.burakpozut.notification_service.app.command.CreateNotificationCommand;
import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateNotificationService {
  private final NotificationRepository notificationRepository;

  public Notification handle(CreateNotificationCommand command) {

    // TODO: check customer and order or dont, and also we might check if the order
    // is the customers
    var notification = Notification.of(command.customerId(), command.orderId(),
        command.type(), command.channel(), command.status());
    var saved = notificationRepository.save(notification);
    return saved;
  }

}

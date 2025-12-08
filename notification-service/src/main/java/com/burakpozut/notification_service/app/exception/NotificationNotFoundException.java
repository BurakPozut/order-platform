package com.burakpozut.notification_service.app.exception;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class NotificationNotFoundException extends NotFoundException {
  public NotificationNotFoundException(UUID id) {
    super("Notification not found with id: " + id);
  }

}

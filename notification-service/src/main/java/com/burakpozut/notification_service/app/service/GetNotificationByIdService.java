package com.burakpozut.notification_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.notification_service.app.exception.NotificationNotFoundException;
import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetNotificationByIdService {
  private final NotificationRepository notificationRepository;

  public Notification handle(UUID id) {
    var notification = notificationRepository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
    return notification;
  }

}

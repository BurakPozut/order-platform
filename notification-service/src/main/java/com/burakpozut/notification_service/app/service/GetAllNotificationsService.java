package com.burakpozut.notification_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllNotificationsService {
  private final NotificationRepository notificationRepository;

  public List<Notification> handle() {
    return notificationRepository.findAll();
  }

}

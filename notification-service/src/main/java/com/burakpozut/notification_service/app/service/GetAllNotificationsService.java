package com.burakpozut.notification_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllNotificationsService {
  private final NotificationRepository notificationRepository;

  public List<Notification> handle() {
    log.info("notification.getAll.start");
    var notifications = notificationRepository.findAll();
    log.info("notification.getAll.completed count={}", notifications.size());
    return notifications;
  }

}

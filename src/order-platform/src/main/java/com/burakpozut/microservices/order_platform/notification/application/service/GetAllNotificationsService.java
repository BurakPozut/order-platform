package com.burakpozut.microservices.order_platform.notification.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform.notification.domain.Notification;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllNotificationsService {
  private final NotificationRepository notificationRepository;

  public List<Notification> handle() {
    return notificationRepository.findAll();
  }

}

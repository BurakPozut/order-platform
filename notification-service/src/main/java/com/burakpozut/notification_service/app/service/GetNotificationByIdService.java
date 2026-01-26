package com.burakpozut.notification_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.notification_service.app.exception.NotificationNotFoundException;
import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetNotificationByIdService {
  private final NotificationRepository notificationRepository;

  public Notification handle(UUID id) {
    log.debug("notification.getById.start notificationId={}", id);
    var notification = notificationRepository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
    log.info("notification.getById.found notificationId={} orderId={} customerId={} type={} status={}",
            notification.id(), notification.orderId(), notification.customerId(),
            notification.type(), notification.status());
    return notification;
  }

}

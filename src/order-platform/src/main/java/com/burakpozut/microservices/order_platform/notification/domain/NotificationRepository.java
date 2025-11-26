package com.burakpozut.microservices.order_platform.notification.domain;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository {
  List<Notification> findAll();

  Notification save(Notification notification);
}

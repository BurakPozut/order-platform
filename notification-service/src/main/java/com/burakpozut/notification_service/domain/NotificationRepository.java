package com.burakpozut.notification_service.domain;

import java.util.List;

public interface NotificationRepository {
  List<Notification> findAll();
}

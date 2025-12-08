package com.burakpozut.notification_service.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
  List<Notification> findAll();

  Optional<Notification> findById(UUID notificationId);
}

package com.burakpozut.notification_service.infra.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {
  private final SpringDataNotificationRepository jpa;

  @Override
  public List<Notification> findAll() {
    return jpa.findAll().stream().map(NotificationMapper::toDomain).toList();
  }

  @Override
  public Optional<Notification> findById(UUID notificationId) {
    return jpa.findById(notificationId).map(NotificationMapper::toDomain);
  }

}

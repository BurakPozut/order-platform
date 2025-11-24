package com.burakpozut.microservices.order_platform.notification.infrastructure.persistance;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.notification.domain.Notification;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {
  private final SpringDataNotificationRepository jpa;

  @Override
  public List<Notification> findAll() {
    return jpa.findAll().stream().map(NotificationMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Notification save(Notification notification) {
    var entity = NotificationMapper.toEntity(notification, true);
    var savedEntity = jpa.save(entity);
    return NotificationMapper.toDomain(savedEntity);
  }
}

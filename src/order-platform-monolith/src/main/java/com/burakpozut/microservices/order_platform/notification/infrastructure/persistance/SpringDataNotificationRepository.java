package com.burakpozut.microservices.order_platform.notification.infrastructure.persistance;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataNotificationRepository extends JpaRepository<NotificationJpaEntity, UUID> {

}

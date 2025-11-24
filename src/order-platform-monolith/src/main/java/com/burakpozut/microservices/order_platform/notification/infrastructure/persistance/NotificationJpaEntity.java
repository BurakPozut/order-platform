package com.burakpozut.microservices.order_platform.notification.infrastructure.persistance;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.burakpozut.microservices.order_platform.notification.domain.NotificationStatus;
import com.burakpozut.microservices.order_platform.notification.domain.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NotificationJpaEntity {

  @Id
  private UUID id;

  @Column(name = "customer_id")
  private UUID customerId;

  @Column(name = "order_id")
  private UUID orderId;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationType type;

  @Column(name = "channed", nullable = false)
  private String channed;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationStatus status;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Transient
  private boolean isNew;
}

package com.burakpozut.notification_service.infra.persistance;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "UUID")
  private UUID id;

  @Column(name = "customer_id", columnDefinition = "UUID")
  private UUID customerId;

  @Column(name = "order_id", columnDefinition = "UUID")
  private UUID orderId;

  @Column(name = "type", nullable = false, length = 20)
  private String type;

  @Column(name = "channel", nullable = false, length = 20)
  private String channel;

  @Column(name = "status", nullable = false, length = 32)
  private String status;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Transient
  private boolean isNew;
}

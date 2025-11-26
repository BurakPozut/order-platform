package com.burakpozut.microservices.order_platform.payment.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.burakpozut.microservices.order_platform.payment.domain.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class PaymentJpaEntiry {
  @Id
  private UUID id;

  @Column(name = "order_id")
  private UUID orderId;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Column(name = "provider")
  private String provider;

  @Column(name = "provider_ref")
  private String providerRef;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Transient
  private boolean isNew;
}

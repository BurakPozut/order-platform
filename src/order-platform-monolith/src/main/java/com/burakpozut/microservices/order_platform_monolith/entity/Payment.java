package com.burakpozut.microservices.order_platform_monolith.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "amount", nullable = false)
  private java.math.BigDecimal amount;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "status", nullable = false, length = 32)
  private String status;

  @Column(name = "provider", nullable = false, length = 64)
  private String provider;

  @Column(name = "provider_ref", length = 128)
  private String providerRef;

  @Column(name = "created_at", nullable = false)
  private java.time.LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private java.time.LocalDateTime updatedAt;
}

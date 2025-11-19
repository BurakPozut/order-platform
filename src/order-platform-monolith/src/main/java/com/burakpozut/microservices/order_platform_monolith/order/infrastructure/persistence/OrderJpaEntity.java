package com.burakpozut.microservices.order_platform_monolith.order.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data // Maybe exclude the created at and updatet at in the equals and hashcode
@NoArgsConstructor
public class OrderJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "customer_id", nullable = false)
  private java.util.UUID customerId;

  @Column(name = "status", nullable = false, length = 32)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private java.math.BigDecimal totalAmount;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}

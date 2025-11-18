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
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "status", nullable = false, length = 32)
  private String status;

  @Column(name = "total_amount", nullable = false)
  private java.math.BigDecimal totalAmount;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "created_at", nullable = false)
  private java.time.LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private java.time.LocalDateTime updatedAt;
}

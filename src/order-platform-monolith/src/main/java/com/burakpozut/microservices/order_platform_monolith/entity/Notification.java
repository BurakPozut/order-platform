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
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;

  @Column(name = "customer_id")
  private UUID customerId;

  @Column(name = "order_id")
  private Long orderId;

  @Column(name = "type", nullable = false, length = 20)
  private String type;

  @Column(name = "channed", nullable = false, length = 20)
  private String channed;

  @Column(name = "status", nullable = false, length = 32)
  private String status;

  @Column(name = "created_at", nullable = false)
  private java.time.LocalDateTime createdAt;
}

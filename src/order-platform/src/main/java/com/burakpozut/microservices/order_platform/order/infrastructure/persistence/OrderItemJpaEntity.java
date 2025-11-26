package com.burakpozut.microservices.order_platform.order.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderItemJpaEntity {
  @Id
  private UUID id;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(name = "product_name", nullable = false)
  private String productName;

  @Column(name = "unit_price", nullable = false)
  private BigDecimal unitPrice;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Transient
  private boolean isNew;
}

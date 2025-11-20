package com.burakpozut.microservices.order_platform_monolith.product.infrastructure.persistance;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
@Table(name = "products")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProductJpaEntity {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private java.util.UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "price", nullable = false)
  private java.math.BigDecimal price;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "status", nullable = false)
  private String status;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private java.time.LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private java.time.LocalDateTime updatedAt;

  @Transient
  private boolean isNew;
}

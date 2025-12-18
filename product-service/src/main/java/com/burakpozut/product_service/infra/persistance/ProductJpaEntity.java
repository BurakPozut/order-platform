package com.burakpozut.product_service.infra.persistance;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.domain.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor()
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

  @Enumerated(EnumType.STRING)
  @Column(name = "currency", nullable = false, length = 3)
  @JdbcTypeCode(java.sql.Types.CHAR)
  private Currency currency;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ProductStatus status;

  @Version
  @Column(name = "version")
  private Long version;

  @Column(name = "inventory")
  private Integer inventory;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Transient
  private boolean isNew;
}

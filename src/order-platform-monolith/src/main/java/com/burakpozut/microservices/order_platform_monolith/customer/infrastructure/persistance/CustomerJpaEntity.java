package com.burakpozut.microservices.order_platform_monolith.customer.infrastructure.persistance;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CustomerJpaEntity {

  @Id
  // @GeneratedValue(strategy = GenerationType.UUID)
  @NonNull
  private UUID id;

  @Column(name = "email", nullable = false, unique = true)
  @NonNull
  private String email;

  @Column(name = "full_name", nullable = false)
  @NonNull
  private String fullName;

  @Column(name = "status", nullable = false)
  private String status;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Transient
  private boolean isNew;

}

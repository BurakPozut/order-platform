package com.burakpozut.microservices.order_platform_monolith.product.infrastructure.persistance;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {

  Optional<ProductJpaEntity> findById(UUID id);

  Optional<ProductJpaEntity> findByName(String name);
}

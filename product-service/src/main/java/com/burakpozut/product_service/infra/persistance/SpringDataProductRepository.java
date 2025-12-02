package com.burakpozut.product_service.infra.persistance;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {
  Optional<ProductJpaEntity> findByName(String name);

  boolean existsByName(String name);
}

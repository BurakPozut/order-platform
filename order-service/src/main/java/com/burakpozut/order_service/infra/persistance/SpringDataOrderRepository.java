package com.burakpozut.order_service.infra.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {

  @EntityGraph(attributePaths = { "items" })
  @Override
  List<OrderJpaEntity> findAll();

  @EntityGraph(attributePaths = { "items" })
  Slice<OrderJpaEntity> findAllBy(Pageable pageable);

  Optional<OrderJpaEntity> findByIdempotencyKey(String idempotencyKey);
}
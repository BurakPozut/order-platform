package com.burakpozut.order_service.infra.persistance;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {

  @EntityGraph(attributePaths = { "items" })
  @Override
  List<OrderJpaEntity> findAll();

}
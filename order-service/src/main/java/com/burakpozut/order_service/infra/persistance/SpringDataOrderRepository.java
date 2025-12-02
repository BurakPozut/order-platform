package com.burakpozut.order_service.infra.persistance;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {

}
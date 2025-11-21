package com.burakpozut.microservices.order_platform_monolith.order.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataOrderItemRepository extends JpaRepository<OrderItemJpaEntity, UUID> {

}

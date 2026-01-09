package com.burakpozut.order_service.infra.persistance;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataOrderConfirmationStateRepository
        extends JpaRepository<OrderConfirmationStateJpaEntity, UUID> {
    Optional<OrderConfirmationStateJpaEntity> findByOrderId(UUID orderId);

    void deleteByOrderId(UUID orderId);

}

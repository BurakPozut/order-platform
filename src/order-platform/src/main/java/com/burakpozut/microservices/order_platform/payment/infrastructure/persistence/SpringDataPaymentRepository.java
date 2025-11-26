package com.burakpozut.microservices.order_platform.payment.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntiry, UUID> {
  Optional<PaymentJpaEntiry> findByOrderId(UUID orderId);

}

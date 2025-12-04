package com.burakpozut.payment_service.infra.persistance;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataPaymentRespository extends JpaRepository<PaymentJpaEntity, UUID> {

}

package com.burakpozut.microservices.order_platform.customer.infrastructure.persistance;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {
  Optional<CustomerJpaEntity> findByFullName(String fullName);

  Optional<CustomerJpaEntity> findByEmail(String email);

}

package com.burakpozut.customer_service.infra.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {
  Optional<CustomerJpaEntity> findByEmail(String email);

  boolean existsByEmail(String email);

}

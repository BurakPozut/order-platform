package com.burakpozut.microservices.order_platform_monolith.customer.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository {
  Optional<Customer> findById(UUID id);

  Optional<Customer> findByFullName(String fullName);

  Optional<Customer> findByEmail(String email);

  Customer save(Customer customer,boolean isNew);
}

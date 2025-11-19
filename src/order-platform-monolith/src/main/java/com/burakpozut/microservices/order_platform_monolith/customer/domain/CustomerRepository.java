package com.burakpozut.microservices.order_platform_monolith.customer.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository {
  Optional<Customer> findById(@NonNull UUID id);

  Optional<Customer> findByFullName(@NonNull String fullName);

  Optional<Customer> findByEmail(@NonNull String email);
}

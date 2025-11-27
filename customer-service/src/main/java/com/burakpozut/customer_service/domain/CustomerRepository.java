package com.burakpozut.customer_service.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
  List<Customer> findAll();

  Optional<Customer> findById(UUID id);

  Optional<Customer> findByEmail(String email);

  boolean existsByEmail(String email);

  Customer save(Customer c, boolean isNew);

}

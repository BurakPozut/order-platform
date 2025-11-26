package com.burakpozut.microservices.order_platform.customer.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository {
  List<Customer> findAll();

  Optional<Customer> findById(UUID id);

  Optional<Customer> findByFullName(String fullName);

  Optional<Customer> findByEmail(String email);

  Customer save(Customer customer, boolean isNew);

  void deleteById(UUID id);
}

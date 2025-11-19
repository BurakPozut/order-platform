package com.burakpozut.microservices.order_platform_monolith.customer.infrastructure.persistance;

import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {
  private final SpringDataCustomerRepository jpa;

  @Override
  public Optional<Customer> findById(@NonNull UUID id) {
    return jpa.findById(id).map(CustomerMapper::toDomain);
  }

  @Override
  public Optional<Customer> findByFullName(@NonNull String fullName) {
    return jpa.findByFullName(fullName).map(CustomerMapper::toDomain);
  }
}

package com.burakpozut.customer_service.infra.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {
  private final SpringDataCustomerRepository jpa;

  @Override
  public Optional<Customer> findById(UUID id) {
    return jpa.findById(id).map(CustomerMapper::toDomain);
  }

}

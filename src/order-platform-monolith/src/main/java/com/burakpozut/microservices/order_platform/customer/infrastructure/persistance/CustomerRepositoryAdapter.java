package com.burakpozut.microservices.order_platform.customer.infrastructure.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.customer.domain.Customer;
import com.burakpozut.microservices.order_platform.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {
  private final SpringDataCustomerRepository jpa;

  @Override
  public List<Customer> findAll() {
    return jpa.findAll().stream().map(CustomerMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Optional<Customer> findById(UUID id) {
    return jpa.findById(id).map(CustomerMapper::toDomain);
  }

  @Override
  public Optional<Customer> findByFullName(String fullName) {
    return jpa.findByFullName(fullName).map(CustomerMapper::toDomain);
  }

  @Override
  public Optional<Customer> findByEmail(String email) {
    return jpa.findByEmail(email).map(CustomerMapper::toDomain);
  }

  @Override
  public Customer save(Customer customer, boolean isNew) {
    var entity = CustomerMapper.toEntity(customer, isNew);
    var savedEntity = jpa.save(entity);
    return CustomerMapper.toDomain(savedEntity);
  }
}

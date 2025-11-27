package com.burakpozut.customer_service.infra.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

  @Override
  public List<Customer> findAll() {
    return jpa.findAll().stream().map(CustomerMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Customer save(Customer c, boolean isNew) {
    var entity = CustomerMapper.toEntity(c, isNew);
    var savedEntity = jpa.save(entity);
    return CustomerMapper.toDomain(savedEntity);
  }

  @Override
  public Optional<Customer> findByEmail(String email) {
    return jpa.findByEmail(email).map(CustomerMapper::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpa.existsByEmail(email);
  }
}

package com.burakpozut.microservices.order_platform_monolith.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform_monolith.dto.CreateCustomerRequest;
import com.burakpozut.microservices.order_platform_monolith.entity.Customer;
import com.burakpozut.microservices.order_platform_monolith.exception.CustomerNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  public List<Customer> findAll() {
    log.info("Fetching all customers");
    return customerRepository.findAll();
  }

  public Customer findById(@NonNull UUID id) {
    log.info("Fetching customer with id: {}", id);
    return customerRepository.findById(id)
        .orElseThrow(() -> new CustomerNotFoundException("Customer with id not found: " + id));
  }

  public Customer findByEmail(@NonNull String email) {
    log.info("Fetching customer wiht email: {}", email);
    return customerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomerNotFoundException("There is no customer with email: " + email));
  }

  public Customer save(CreateCustomerRequest request) {
    log.info("Saving customer: {}", request.getEmail());
    return customerRepository.save(request.toEntity());
  }

  public void deleteById(@NonNull UUID id) {
    log.info("Deleting customer with id: {}", id);
    customerRepository.deleteById(id);
  }
}

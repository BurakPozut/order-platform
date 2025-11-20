package com.burakpozut.microservices.order_platform_monolith.customer.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateCustomerService {
  private final CustomerRepository customerRepository;

  @Transactional
  public Customer hande( String fullName, String email) {
    customerRepository.findByEmail(email).ifPresent(c -> {
      throw new IllegalArgumentException("Email already in use: " + email);
    });

    Customer customer = Customer.createNew(fullName, email);
    return customerRepository.save(customer,true);
  }

}

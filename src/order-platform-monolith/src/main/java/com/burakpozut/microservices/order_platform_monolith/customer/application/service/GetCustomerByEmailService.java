package com.burakpozut.microservices.order_platform_monolith.customer.application.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCustomerByEmailService {

  private final CustomerRepository customerRepository;

  public Customer handle(@NonNull String email) {
    return customerRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("No Customer by email:" + email));

  }
}

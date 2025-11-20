package com.burakpozut.microservices.order_platform_monolith.customer.application.service;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform_monolith.customer.application.exception.CustomerNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCustomerByEmailService {

  private final CustomerRepository customerRepository;

  public Customer handle(String email) {
    return customerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomerNotFoundException(email));

  }
}

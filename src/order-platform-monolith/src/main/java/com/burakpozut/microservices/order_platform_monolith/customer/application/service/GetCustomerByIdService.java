package com.burakpozut.microservices.order_platform_monolith.customer.application.service;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform_monolith.customer.application.exception.CustomerNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.customer.application.query.GetCusotmerDetailsQuery;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;
import com.burakpozut.microservices.order_platform_monolith.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCustomerByIdService {
  private final CustomerRepository customerRepository;

  public Customer handle(GetCusotmerDetailsQuery query) {
    return customerRepository.findById(query.getCustomerId())
        .orElseThrow(() -> new CustomerNotFoundException(query.getCustomerId()));

  }

}

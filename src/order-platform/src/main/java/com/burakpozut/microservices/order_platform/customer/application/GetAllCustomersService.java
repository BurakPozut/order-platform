package com.burakpozut.microservices.order_platform.customer.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform.customer.domain.Customer;
import com.burakpozut.microservices.order_platform.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllCustomersService {
  private final CustomerRepository customerRepository;

  public List<Customer> handle() {
    return customerRepository.findAll();
  }

}

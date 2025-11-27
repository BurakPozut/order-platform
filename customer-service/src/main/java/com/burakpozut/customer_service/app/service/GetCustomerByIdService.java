package com.burakpozut.customer_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.customer_service.app.exception.CustomerNotFoundException;
import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCustomerByIdService {
  private final CustomerRepository customerRepository;

  public Customer handle(UUID id) {
    return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
  }

}

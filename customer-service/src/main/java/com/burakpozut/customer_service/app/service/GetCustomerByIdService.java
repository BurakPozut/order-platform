package com.burakpozut.customer_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.customer_service.app.exception.CustomerNotFoundException;
import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCustomerByIdService {
  private final CustomerRepository customerRepository;

  public Customer handle(UUID id) {
    log.debug("Looking up customer with ID: {}", id);
    return customerRepository.findById(id).orElseThrow(() -> {
      log.warn("Customer not found with ID: {}", id);
      return new CustomerNotFoundException(id);
    });
  }

}

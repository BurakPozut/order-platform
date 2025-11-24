package com.burakpozut.microservices.order_platform.customer.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.customer.application.exception.CustomerNotFoundException;
import com.burakpozut.microservices.order_platform.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteCustomerService {
  private final CustomerRepository customerRepository;

  @Transactional
  public void handle(UUID id) {
    if (!customerRepository.findById(id).isPresent()) {
      throw new CustomerNotFoundException(id);
    }
    customerRepository.deleteById(id);
  }

}

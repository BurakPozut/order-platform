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
    log.debug("customer.getById.start customerId={}", id);
    var customer = customerRepository.findById(id).orElseThrow(() -> {
      log.warn("customer.getById.not_found customerId={}", id);
      return new CustomerNotFoundException(id);
    });
    log.info("customer.getById.found customerId={} email={} fullName={}",
            customer.id(), customer.email(), customer.fullName());
    return customer;
  }

}

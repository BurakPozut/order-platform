package com.burakpozut.customer_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllCustomersService {
  private final CustomerRepository customerRepository;

  public List<Customer> handle() {
    log.info("customer.getAll.start");
    var customers = customerRepository.findAll();
    log.info("customer.getAll.completed count={}", customers.size());
    return customers;
  }

}

package com.burakpozut.customer_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllCustomersService {
  private final CustomerRepository customerRepository;

  public List<Customer> handle() {
    return customerRepository.findAll();
  }

}

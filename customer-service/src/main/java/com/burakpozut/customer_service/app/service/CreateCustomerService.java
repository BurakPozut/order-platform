package com.burakpozut.customer_service.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.customer_service.app.command.CreateCustomerCommand;
import com.burakpozut.customer_service.app.exception.EmailAlreadyInUseException;
import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateCustomerService {
  private final CustomerRepository customerRepository;

  @Transactional
  public Customer handle(CreateCustomerCommand command) {
    if (customerRepository.existsByEmail(command.email()))
      throw new EmailAlreadyInUseException(command.email());

    var customer = Customer.createNew(command.fullName(), command.email());
    return customerRepository.save(customer, true);

  }

}

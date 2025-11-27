package com.burakpozut.customer_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.customer_service.app.command.UpdateCustomerCommand;
import com.burakpozut.customer_service.app.exception.CustomerNotFoundException;
import com.burakpozut.customer_service.app.exception.EmailAlreadyInUseException;
import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateCustomerService {
  private final CustomerRepository customerRepository;

  @Transactional
  public Customer handle(UUID id, UpdateCustomerCommand command) {
    var existing = customerRepository.findById(id)
        .orElseThrow(() -> new CustomerNotFoundException(id));

    if (!existing.email().equals(command.email()) && customerRepository.existsByEmail(command.email())) {
      throw new EmailAlreadyInUseException(command.email());
    }

    var updated = Customer.rehydrate(id, command.fullName(), command.email());
    return customerRepository.save(updated, false);

  }

}

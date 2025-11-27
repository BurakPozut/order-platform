package com.burakpozut.customer_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.customer_service.app.command.PatchCustomerCommand;
import com.burakpozut.customer_service.app.exception.CustomerNotFoundException;
import com.burakpozut.customer_service.app.exception.EmailAlreadyInUseException;
import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatchCustomerService {
  private final CustomerRepository customerRepository;

  public Customer handle(UUID id, PatchCustomerCommand command) {
    var existing = customerRepository.findById(id)
        .orElseThrow(() -> new CustomerNotFoundException(id));

    String newFullName = command.fullName() != null ? command.fullName() : existing.fullName();
    String newEmail = command.email() != null ? command.email() : existing.email();

    if (command.email() != null && !existing.email().equals(newEmail) && customerRepository.existsByEmail(newEmail)) {
      throw new EmailAlreadyInUseException(newEmail);
    }

    var updated = Customer.rehydrate(id, newFullName, newEmail);
    return customerRepository.save(updated, false);
  }

}

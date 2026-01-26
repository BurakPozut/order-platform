package com.burakpozut.customer_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.customer_service.app.command.PatchCustomerCommand;
import com.burakpozut.customer_service.app.exception.CustomerNotFoundException;
import com.burakpozut.customer_service.app.exception.EmailAlreadyInUseException;
import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatchCustomerService {
  private final CustomerRepository customerRepository;

  public Customer handle(UUID id, PatchCustomerCommand command) {
    log.info("customer.patch.start customerId={} email={} fullName={}",
            id, command.email(), command.fullName());

    var existing = customerRepository.findById(id)
        .orElseThrow(() -> new CustomerNotFoundException(id));

    String newFullName = command.fullName() != null ? command.fullName() : existing.fullName();
    String newEmail = command.email() != null ? command.email() : existing.email();

    if (command.email() != null && !existing.email().equals(newEmail) && customerRepository.existsByEmail(newEmail)) {
      log.warn("customer.patch.email_conflict customerId={} newEmail={}",
              id, newEmail);
      throw new EmailAlreadyInUseException(newEmail);
    }

    var updated = Customer.rehydrate(id, newFullName, newEmail);
    var saved = customerRepository.save(updated, false);
    log.info("customer.patch.completed customerId={} email={} fullName={}",
            saved.id(), saved.email(), saved.fullName());
    return saved;
  }

}

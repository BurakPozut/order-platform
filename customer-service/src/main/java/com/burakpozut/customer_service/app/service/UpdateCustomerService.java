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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateCustomerService {
  private final CustomerRepository customerRepository;

  @Transactional
  public Customer handle(UUID id, UpdateCustomerCommand command) {
    log.info("customer.update.start customerId={} email={} fullName={}",
            id, command.email(), command.fullName());

    var existing = customerRepository.findById(id)
        .orElseThrow(() -> new CustomerNotFoundException(id));

    if (!existing.email().equals(command.email()) && customerRepository.existsByEmail(command.email())) {
      log.warn("customer.update.email_conflict customerId={} newEmail={}",
              id, command.email());
      throw new EmailAlreadyInUseException(command.email());
    }

    var updated = Customer.rehydrate(id, command.fullName(), command.email());
    var saved = customerRepository.save(updated, false);
    log.info("customer.update.completed customerId={} email={} fullName={}",
            saved.id(), saved.email(), saved.fullName());
    return saved;

  }

}

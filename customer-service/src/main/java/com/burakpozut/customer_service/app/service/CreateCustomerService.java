package com.burakpozut.customer_service.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.customer_service.app.command.CreateCustomerCommand;
import com.burakpozut.customer_service.app.exception.EmailAlreadyInUseException;
import com.burakpozut.customer_service.domain.Customer;
import com.burakpozut.customer_service.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCustomerService {
  private final CustomerRepository customerRepository;

  @Transactional
  public Customer handle(CreateCustomerCommand command) {
    log.info("customer.create.start email={} fullName={}", command.email(), command.fullName());

    if (customerRepository.existsByEmail(command.email())) {
      log.warn("customer.create.email_conflict email={}", command.email());
      throw new EmailAlreadyInUseException(command.email());
    }

    var customer = Customer.createNew(command.fullName(), command.email());
    var saved = customerRepository.save(customer, true);
    log.info("customer.create.completed customerId={} email={} fullName={}",
            saved.id(), saved.email(), saved.fullName());
    return saved;

  }

}

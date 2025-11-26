package com.burakpozut.microservices.order_platform.payment.infrastructure.customer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.customer.domain.Customer;
import com.burakpozut.microservices.order_platform.customer.domain.CustomerRepository;
import com.burakpozut.microservices.order_platform.payment.domain.port.CustomerGateway;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerGatewayAdapter implements CustomerGateway {
  private final CustomerRepository customerRepository;

  @Override
  public Optional<Customer> getCusotmerById(UUID id) {
    return customerRepository.findById(id);
  }

}

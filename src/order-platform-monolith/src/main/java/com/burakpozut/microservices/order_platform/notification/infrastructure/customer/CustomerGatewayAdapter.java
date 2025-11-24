package com.burakpozut.microservices.order_platform.notification.infrastructure.customer;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.customer.domain.CustomerRepository;
import com.burakpozut.microservices.order_platform.notification.domain.port.CustomerGateway;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerGatewayAdapter implements CustomerGateway {
  private final CustomerRepository customerRepository;

  @Override
  public boolean customerExists(UUID id) {
    return customerRepository.findById(id).isPresent();
  }
}

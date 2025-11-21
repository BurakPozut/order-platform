package com.burakpozut.microservices.order_platform_monolith.order.infrastructure.customer;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform_monolith.customer.domain.CustomerRepository;
import com.burakpozut.microservices.order_platform_monolith.order.domain.port.CustomerGateway;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CusotmerGatewayAdapter implements CustomerGateway {
  private final CustomerRepository customerRepository;

  @Override
  public boolean customerExists(UUID id) {
    return customerRepository.findById(id).isPresent();
  }

}

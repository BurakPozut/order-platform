package com.burakpozut.microservices.order_platform.payment.domain.port;

import java.util.Optional;
import java.util.UUID;

import com.burakpozut.microservices.order_platform.customer.domain.Customer;

public interface CustomerGateway {

  Optional<Customer> getCusotmerById(UUID id);
}

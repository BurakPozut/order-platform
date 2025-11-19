package com.burakpozut.microservices.order_platform_monolith.customer.infrastructure.persistance;

import org.springframework.lang.NonNull;

import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;

public class CustomerMapper {
  @NonNull
  static Customer toDomain(CustomerJpaEntity entity) {
    return new Customer(entity.getId(), entity.getEmail(), entity.getFullName());
  }
}

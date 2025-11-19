package com.burakpozut.microservices.order_platform_monolith.customer.infrastructure.persistance;

import org.springframework.lang.NonNull;

import com.burakpozut.microservices.order_platform_monolith.customer.domain.Customer;

public class CustomerMapper {
  @NonNull
  static Customer toDomain(CustomerJpaEntity entity) {
    return Customer.rehydrate(entity.getId(), entity.getEmail(), entity.getFullName());
  }

  @NonNull
  static CustomerJpaEntity toEntity(Customer c) {
    var entity = new CustomerJpaEntity();
    entity.setId(c.getId());
    entity.setEmail(c.getEmail());
    entity.setFullName(c.getFullName());
    entity.setNew(true);
    entity.setStatus("ACTIVE");
    return entity;

  }
}

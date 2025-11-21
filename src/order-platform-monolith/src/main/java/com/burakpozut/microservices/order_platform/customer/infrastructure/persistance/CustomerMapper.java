package com.burakpozut.microservices.order_platform.customer.infrastructure.persistance;

import com.burakpozut.microservices.order_platform.customer.domain.Customer;

public class CustomerMapper {
  static Customer toDomain(CustomerJpaEntity entity) {
    return Customer.rehydrate(entity.getId(), entity.getEmail(), entity.getFullName());
  }

  static CustomerJpaEntity toEntity(Customer c, boolean isNew) {
    var entity = new CustomerJpaEntity();
    entity.setId(c.getId());
    entity.setEmail(c.getEmail());
    entity.setFullName(c.getFullName());
    entity.setNew(isNew);
    entity.setStatus("ACTIVE");
    return entity;

  }
}

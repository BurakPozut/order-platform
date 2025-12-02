package com.burakpozut.customer_service.infra.persistence;

import com.burakpozut.customer_service.domain.Customer;

public class CustomerMapper {

  static Customer toDomain(CustomerJpaEntity entity) {
    return Customer.rehydrate(entity.getId(), entity.getFullName(), entity.getEmail());
  }

  static CustomerJpaEntity toEntity(Customer c, boolean isNew) {
    var entity = new CustomerJpaEntity();
    entity.setId(c.id());
    entity.setEmail(c.email());
    entity.setFullName(c.fullName());
    entity.setStatus("ACTIVE");
    entity.setNew(isNew);
    return entity;
  }

}

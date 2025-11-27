package com.burakpozut.customer_service.domain;

import java.util.UUID;

import com.burakpozut.common.exception.DomainValidationException;

public record Customer(
    UUID id,
    String email,
    String fullName) {

  public Customer {
    if (id == null)
      throw new DomainValidationException("Id can not be null");
    if (fullName == null)
      throw new DomainValidationException("Full name can not be null");
    if (email == null)
      throw new DomainValidationException("Email can not be null");
  }

  public static Customer createNew(String fullName, String email) {
    UUID id = UUID.randomUUID();

    return new Customer(id, email, fullName);
  }

  public static Customer rehydrate(UUID id, String fullName, String email) {
    return new Customer(id, email, fullName);
  }

}

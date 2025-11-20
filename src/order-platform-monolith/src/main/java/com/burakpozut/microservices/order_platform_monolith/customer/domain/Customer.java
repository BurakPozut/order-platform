package com.burakpozut.microservices.order_platform_monolith.customer.domain;

import java.util.UUID;


import lombok.Getter;

@Getter
public class Customer {
  
  private final UUID id;
 final String email;
  
  private final String fullName;

  private Customer(UUID id, String fullName, String email) {
    if (id == null)
      throw new IllegalArgumentException("id cannot be null");
    if (fullName == null)
      throw new IllegalArgumentException("Full name cannot be null");
    if (email == null)
      throw new IllegalArgumentException("Email cannot be null");

    this.id = id;
    this.email = email;
    this.fullName = fullName;
  }

  public static Customer createNew(String fullName, String email) {
    UUID id = UUID.randomUUID();

    return new Customer(id, fullName, email);
  }

  
  public static Customer rehydrate(UUID id, String fullName, String email) {
    return new Customer(id, fullName, email);
  }
}

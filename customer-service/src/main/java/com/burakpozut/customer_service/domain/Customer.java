package com.burakpozut.customer_service.domain;

import java.util.UUID;

public record Customer(
    UUID id,
    String email,
    String fullName) {

  public Customer {

  }

}

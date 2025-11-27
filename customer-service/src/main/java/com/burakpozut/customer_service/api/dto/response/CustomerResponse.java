package com.burakpozut.customer_service.api.dto.response;

import java.util.UUID;

import com.burakpozut.customer_service.domain.Customer;

public record CustomerResponse(UUID id, String fullName, String email) {

  public static CustomerResponse from(
      Customer c) {
    return new CustomerResponse(c.id(), c.fullName(), c.email());
  }
}

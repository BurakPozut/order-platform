package com.burakpozut.microservices.order_platform_monolith.customer.domain;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Customer {
  private final UUID id;
  private final String email;
  private final String fullName;
}

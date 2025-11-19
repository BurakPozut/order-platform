package com.burakpozut.microservices.order_platform_monolith.customer.api.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomerResponse {
  private final UUID id;
  private final String fullName;
  private final String email;

}

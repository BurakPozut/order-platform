package com.burakpozut.microservices.order_platform_monolith.customer.application.query;

import java.util.UUID;

import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetCusotmerDetailsQuery {
  @NonNull
  private final UUID customerId;

}

package com.burakpozut.microservices.order_platform_monolith.customer.application.query;

import java.util.UUID;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GetCusotmerDetailsQuery {
  private final UUID customerId;

}

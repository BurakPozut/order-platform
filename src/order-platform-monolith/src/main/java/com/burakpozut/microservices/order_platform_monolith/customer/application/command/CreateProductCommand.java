package com.burakpozut.microservices.order_platform_monolith.customer.application.command;

import java.math.BigDecimal;

import com.burakpozut.microservices.order_platform_monolith.common.domain.Currency;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateProductCommand {
  private final String name;
  private final BigDecimal price;
  private final Currency currency;
  private final ProductStatus status;
}

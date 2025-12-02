package com.burakpozut.product_service.app.command;

import java.math.BigDecimal;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.domain.ProductStatus;

public record PatchProductCommand(
    String name,
    BigDecimal price,
    Currency currency,
    ProductStatus status) {
  public static PatchProductCommand of(String name, BigDecimal price,
      Currency currency, ProductStatus status) {
    return new PatchProductCommand(name, price, currency, status);
  }

}

package com.burakpozut.product_service.app.command;

import java.math.BigDecimal;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.domain.ProductStatus;

public record UpdateProductCommand(
  String name,
  BigDecimal price,
  Currency currency,
  ProductStatus status
) {
  
  public static UpdateProductCommand of(String name, BigDecimal price, Currency currency, ProductStatus status){
    return new UpdateProductCommand(name, price,currency,status);
  }
}

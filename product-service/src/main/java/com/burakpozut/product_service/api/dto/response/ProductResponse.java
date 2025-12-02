package com.burakpozut.product_service.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductStatus;

public record ProductResponse(
    UUID id,
    String name,
    BigDecimal price,
    Currency currency,
    ProductStatus status) {
  public static ProductResponse from(
      Product p) {
    return new ProductResponse(p.id(), p.name(), p.price(), p.currency(), p.status());
  }
}

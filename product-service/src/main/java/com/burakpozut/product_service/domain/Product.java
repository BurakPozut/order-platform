package com.burakpozut.product_service.domain;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.DomainValidationException;
import java.math.BigDecimal;
import java.util.UUID;

public record Product(
  UUID id,
  String name,
  BigDecimal price,
  Currency currency,
  ProductStatus status
) {
  public Product {
    if (id == null) throw new DomainValidationException("Product Id can not be null");
    if (name == null) throw new DomainValidationException( "Product name can not be null");
    if (price == null) throw new DomainValidationException("Price can not be null");
    if (price.compareTo(BigDecimal.ZERO) <= 0) throw new DomainValidationException("Price must be greater than 0");
    if (currency == null) throw new DomainValidationException("Currency can not be null");
    if (status == null) throw new DomainValidationException("status can not be null");
  }

  public static Product createNew(
    String name,
    BigDecimal price,
    Currency currency,
    ProductStatus status
  ) {
    return new Product(UUID.randomUUID(), name, price, currency, status);
  }

  public static Product rehydrate(
    UUID id,
    String name,
    BigDecimal price,
    Currency currency,
    ProductStatus status
  ) {
    return new Product(id, name, price, currency, status);
  }
}

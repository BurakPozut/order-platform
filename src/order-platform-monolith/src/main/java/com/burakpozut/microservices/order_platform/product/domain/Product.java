package com.burakpozut.microservices.order_platform.product.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.domain.Currency;
import com.burakpozut.microservices.order_platform.common.exception.DomainValidationException;

import lombok.Getter;

@Getter
public class Product {
  private final UUID id;
  private final String name;
  private final BigDecimal price;
  private final Currency currency;
  private final ProductStatus status;

  private Product(UUID id, String name, BigDecimal price, Currency currency, ProductStatus status) {
    if (id == null)
      throw new DomainValidationException("Product Id can not be null");
    if (name == null)
      throw new DomainValidationException("Product name can not be null");
    if (price == null)
      throw new DomainValidationException("Price can not be null");
    if (price.compareTo(BigDecimal.ZERO) <= 0)
      throw new DomainValidationException("Price must be greater than 0");
    if (currency == null)
      throw new DomainValidationException("Currency can not be null");
    if (status == null)
      throw new DomainValidationException("status can not be null");

    this.id = id;
    this.name = name;
    this.price = price;
    this.currency = currency;
    this.status = status;
  }

  public static Product createNew(String name, BigDecimal price, Currency currency, ProductStatus status) {
    return new Product(UUID.randomUUID(), name, price, currency, status);
  }

  public static Product rehydrate(UUID id, String name, BigDecimal price, Currency currency, ProductStatus status) {
    return new Product(id, name, price, currency, status);
  }

}
package com.burakpozut.order_service.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.DomainValidationException;

public record ProductInfo(
    UUID productId,
    String name,
    BigDecimal price,
    Currency currency) {

  public ProductInfo {
    if (productId == null)
      throw new DomainValidationException("Product Id can not be null");
    if (name == null || name.isBlank())
      throw new DomainValidationException("Product name cannot be null or blank");
    if (price == null)
      throw new DomainValidationException("Product price cannot be null");
    if (price.compareTo(BigDecimal.ZERO) <= 0)
      throw new DomainValidationException("Product price must be greater than 0");
    if (currency == null)
      throw new DomainValidationException("Product currency cannot be null");
  }
}

package com.burakpozut.order_service.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.DomainValidationException;

public record Order(
    UUID id,
    UUID customerId,
    OrderStatus status,
    BigDecimal totalAmount,
    Currency currency) {
  public Order {
    if (id == null)
      throw new DomainValidationException("Order Id cannot be null");
    if (customerId == null)
      throw new DomainValidationException("Customer Id cannot be null");
    if (status == null)
      throw new DomainValidationException("Order status cannot be null or blank");
    if (totalAmount == null)
      throw new DomainValidationException("Total amount cannot be null");
    if (totalAmount.compareTo(BigDecimal.ZERO) < 0)
      throw new DomainValidationException("Total amount cannot be negative");
    if (currency == null)
      throw new DomainValidationException("Currency cannot be null");
  }

  public static Order of(UUID customerId, OrderStatus status, BigDecimal totalAmount, Currency currency) {
    return new Order(
        UUID.randomUUID(),
        customerId,
        status,
        totalAmount,
        currency);
  }

  public static Order rehydrate(
      UUID id,
      UUID customerId,
      OrderStatus status,
      BigDecimal totalAmount,
      Currency currency) {
    return new Order(id, customerId, status, totalAmount, currency);
  }
}

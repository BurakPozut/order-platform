package com.burakpozut.microservices.order_platform_monolith.order.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.common.domain.Currency;
import com.burakpozut.microservices.order_platform_monolith.common.exception.DomainValidationException;

import lombok.Getter;

@Getter
public class Order {
  private final UUID id;
  private final UUID customerId;
  private final OrderStatus orderStatus;
  private final BigDecimal totalAmount;
  private final Currency currency;

  private Order(UUID id, UUID customerId, OrderStatus orderStatus, BigDecimal totalAmount, Currency currency) {
    if (id == null) {
      throw new DomainValidationException("Order id cannot be null");
    }
    if (customerId == null) {
      throw new DomainValidationException("Customer id cannot be null");
    }
    if (orderStatus == null) {
      throw new DomainValidationException("Order status cannot be null");
    }
    if (totalAmount == null)
      throw new DomainValidationException("Total amount cannot be null");
    if (totalAmount.compareTo(BigDecimal.ZERO) < 0)
      throw new DomainValidationException("Total amount cannot be negative");
    if (currency == null)
      throw new DomainValidationException("Currency cannot be null");
    this.id = id;
    this.customerId = customerId;
    this.orderStatus = orderStatus;
    this.totalAmount = totalAmount;
    this.currency = currency;
  }

  public static Order createNew(UUID customerId, OrderStatus orderStatus, BigDecimal totalAmount, Currency currency) {
    return new Order(UUID.randomUUID(), customerId, orderStatus, totalAmount, currency);
  }

  public static Order rehydrate(UUID id, UUID customerId, OrderStatus orderStatus, BigDecimal totalAmount,
      Currency currency) {
    return new Order(id, customerId, orderStatus, totalAmount, currency);
  }
}

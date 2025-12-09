package com.burakpozut.order_service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.DomainValidationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Order(
    UUID id,
    UUID customerId,
    OrderStatus status,
    BigDecimal totalAmount,
    Currency currency,
    List<OrderItem> items,
    String idempotencyKey,
    LocalDateTime updatedAt) {
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
    if (items == null)
      throw new DomainValidationException("Items can not be null");
    if (idempotencyKey == null)
      throw new DomainValidationException("IdempotencyKey can't be null");// TODO: is this domain exception or should we
                                                                          // retrun this to the client
  }

  public static Order of(UUID customerId, OrderStatus status,
      BigDecimal totalAmount, Currency currency, List<OrderItem> items, String idempotencyKey) {

    log.debug("idempotency key in the object: " + idempotencyKey);
    return new Order(
        UUID.randomUUID(),
        customerId,
        status,
        totalAmount,
        currency,
        items,
        idempotencyKey,
        null);
  }

  public static Order rehydrate(
      UUID id,
      UUID customerId,
      OrderStatus status,
      BigDecimal totalAmount,
      Currency currency,
      List<OrderItem> items, String idempotencyKey, LocalDateTime updatedAt) {
    return new Order(id, customerId, status,
        totalAmount, currency, items, idempotencyKey, updatedAt);
  }
}

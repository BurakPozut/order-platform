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
      throw new DomainValidationException("IdempotencyKey can't be null");

  }

  public BigDecimal calculateTotal() {
    return items.stream().map(
        item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public static Order createFrom(
      UUID customerId,
      OrderStatus status,
      Currency currency,
      List<OrderItem> items,
      String idempotencyKey) {
    if (items == null || items.isEmpty()) {
      throw new DomainValidationException("Order must have at least one item");
    }

    BigDecimal totalAmount = items.stream()
        .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return Order.of(customerId, status, totalAmount, currency, items, idempotencyKey);
  }

  // #region Factory Methods
  public static Order of(UUID customerId, OrderStatus status,
      BigDecimal totalAmount, Currency currency, List<OrderItem> items, String idempotencyKey) {

    log.debug("order.create.idempotency_key idempotencyKey={}", idempotencyKey);
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
  // #endregion
}

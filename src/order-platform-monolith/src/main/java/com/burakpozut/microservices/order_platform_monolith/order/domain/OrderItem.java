package com.burakpozut.microservices.order_platform_monolith.order.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.common.exception.DomainValidationException;

import lombok.Getter;

@Getter
public class OrderItem {
  private final UUID id;
  private final UUID orderId;
  private final UUID productId;
  private final String productName;
  private final BigDecimal unitPrice;
  private final int quantity;

  private OrderItem(UUID id, UUID orderId, UUID productId, String productName, BigDecimal unitPrice, int quantity) {
    if (id == null) {
      throw new DomainValidationException("Order item id cannot be null");
    }
    if (orderId == null) {
      throw new DomainValidationException("Order id cannot be null");
    }
    if (productId == null) {
      throw new DomainValidationException("Product id cannot be null");
    }
    if (productName == null || productName.trim().isEmpty()) {
      throw new DomainValidationException("Product name cannot be null or empty");
    }
    if (unitPrice == null) {
      throw new DomainValidationException("Unit price cannot be null");
    }
    if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new DomainValidationException("Unit price cannot be negative");
    }
    if (quantity <= 0) {
      throw new DomainValidationException("Quantity must be positive");
    }
    this.id = id;
    this.orderId = orderId;
    this.productId = productId;
    this.productName = productName;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
  }

  public static OrderItem createNew(UUID orderId, UUID productId, String productName, BigDecimal unitPrice,
      int quantity) {
    return new OrderItem(UUID.randomUUID(), orderId, productId, productName, unitPrice, quantity);
  }

  public static OrderItem rehydrate(UUID id, UUID orderId, UUID productId, String productName, BigDecimal unitPrice,
      int quantity) {
    return new OrderItem(id, orderId, productId, productName, unitPrice, quantity);
  }
}

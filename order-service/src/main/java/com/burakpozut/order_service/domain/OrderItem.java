package com.burakpozut.order_service.domain;

import java.math.BigDecimal;
import java.util.UUID;
import com.burakpozut.common.exception.DomainValidationException;

public record OrderItem(
    UUID id,
    UUID productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity) {
  public OrderItem {
    if (id == null)
      throw new DomainValidationException("OrderItem Id cannot be null");
    if (productId == null)
      throw new DomainValidationException("Product Id cannot be null");
    if (productName == null || productName.isBlank())
      throw new DomainValidationException("Product name cannot be null or blank");
    if (unitPrice == null)
      throw new DomainValidationException("Unit price cannot be null");
    if (unitPrice.compareTo(BigDecimal.ZERO) <= 0)
      throw new DomainValidationException("Unit price must be greater than 0");
    if (quantity == null || quantity <= 0)
      throw new DomainValidationException("Quantity must be greater than 0");
  }

  public static OrderItem rehydrate(
      UUID id,
      UUID productId,
      String productName,
      BigDecimal unitPrice,
      Integer quantity) {
    return new OrderItem(id, productId, productName, unitPrice, quantity);
  }
}

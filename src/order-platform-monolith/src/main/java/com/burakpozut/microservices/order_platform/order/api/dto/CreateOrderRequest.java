package com.burakpozut.microservices.order_platform.order.api.dto;

import java.util.List;
import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.domain.Currency;
import com.burakpozut.microservices.order_platform.common.exception.DomainValidationException;
import com.burakpozut.microservices.order_platform.order.domain.OrderStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
    @NotNull UUID customerId,
    OrderStatus status,
    @NotNull Currency currency,
    @NotNull @NotEmpty List<OrderItemRequest> items) {

  public CreateOrderRequest {
    if (items == null || items.isEmpty()) {
      throw new DomainValidationException("Items in the order can no be empty");
    }

    items = List.copyOf(items);

  }

  public record OrderItemRequest(
      @NotNull UUID productId,
      @NotNull @Positive Integer quantity) {
  }
}

// TODO: Problem with valid annotation with enums
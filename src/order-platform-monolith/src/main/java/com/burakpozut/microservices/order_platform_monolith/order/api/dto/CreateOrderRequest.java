package com.burakpozut.microservices.order_platform_monolith.order.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
    @NotNull UUID customerId,
    OrderStatus status,
    @NotNull @Positive BigDecimal totalAmount,
    @NotNull String currency) {

}

package com.burakpozut.microservices.order_platform_monolith.order.api.dto;

import java.util.List;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
        @NotNull UUID customerId,
        OrderStatus status,
        @NotNull String currency,
        @NotNull @NotEmpty List<OrderItemRequest> items) {

    public record OrderItemRequest(
            @NotNull UUID productId,
            @NotNull @Positive Integer quantity) {
    }

}

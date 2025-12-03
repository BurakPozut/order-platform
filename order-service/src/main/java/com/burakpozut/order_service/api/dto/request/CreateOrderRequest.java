package com.burakpozut.order_service.api.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.domain.OrderItem;
import com.burakpozut.order_service.domain.OrderStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
    @NotNull UUID customerId,
    @NotNull OrderStatus status,
    @Positive BigDecimal totalAmount,
    @NotNull Currency currency,
    @NotEmpty List<OrderItem> items) {

}

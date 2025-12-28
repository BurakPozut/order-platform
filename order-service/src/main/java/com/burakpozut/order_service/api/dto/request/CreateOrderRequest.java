package com.burakpozut.order_service.api.dto.request;

import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull @Schema(description = "Customer ID", defaultValue = "cb8f1671-999e-43cc-8b56-8a561bf92a3e") UUID customerId,
    @NotNull Currency currency,
    @NotEmpty List<OrderItemRequest> items) {

}

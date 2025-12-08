package com.burakpozut.order_service.api.dto.request;

import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull UUID customerId,
        @NotNull Currency currency,
        @NotEmpty List<OrderItemRequest> items) {

}

package com.burakpozut.order_service.api.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
    @NotNull UUID productId,
    @Positive Integer quantity) {

}

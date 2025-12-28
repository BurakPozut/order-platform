package com.burakpozut.order_service.api.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
                @NotNull @Schema(description = "Product ID", defaultValue = "ede75b78-b72d-4d7b-870f-494c91b66aca") UUID productId,
                @Positive Integer quantity) {

}

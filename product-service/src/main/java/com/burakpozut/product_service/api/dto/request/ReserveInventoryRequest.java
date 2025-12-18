package com.burakpozut.product_service.api.dto.request;

import jakarta.validation.constraints.Min;

public record ReserveInventoryRequest(
                @Min(1) int quantity) {
}

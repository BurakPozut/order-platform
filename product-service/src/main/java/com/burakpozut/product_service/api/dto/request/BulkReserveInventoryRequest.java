package com.burakpozut.product_service.api.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record BulkReserveInventoryRequest(
    @NotEmpty @Valid List<ReservationItem> items) {

  public record ReservationItem(
      UUID productId,
      @Min(1) Integer quantity) {
  }
}

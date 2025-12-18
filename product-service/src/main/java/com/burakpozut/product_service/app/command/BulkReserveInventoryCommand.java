package com.burakpozut.product_service.app.command;

import java.util.List;
import java.util.UUID;

public record BulkReserveInventoryCommand(
    List<ReservationItem> items) {

  public record ReservationItem(UUID productId, Integer quantity) {
  }
}
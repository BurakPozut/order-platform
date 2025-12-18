package com.burakpozut.product_service.app.command;

import java.util.UUID;

public record ReserveInventoryCommand(UUID productId, int quantity) {
  public static ReserveInventoryCommand of(UUID productId, int quantity) {
    return new ReserveInventoryCommand(productId, quantity);
  }
}
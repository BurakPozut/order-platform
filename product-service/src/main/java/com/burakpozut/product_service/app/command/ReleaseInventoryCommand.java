package com.burakpozut.product_service.app.command;

import java.util.UUID;

public record ReleaseInventoryCommand(
    UUID productId,
    int quantity) {

  public static ReleaseInventoryCommand of(UUID productId,
      int quantity) {
    return new ReleaseInventoryCommand(productId, quantity);
  }
}

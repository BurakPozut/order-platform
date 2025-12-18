package com.burakpozut.product_service.app.exception;

import java.util.UUID;

public class InsufficentInventoryException extends RuntimeException {
  public InsufficentInventoryException(UUID productId) {
    super("Insufficent inventory for product: " + productId);
  }

}

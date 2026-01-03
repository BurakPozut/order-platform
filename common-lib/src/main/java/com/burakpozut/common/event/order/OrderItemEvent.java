package com.burakpozut.common.event.order;

import java.util.UUID;

public record OrderItemEvent(UUID productId, int quantity) {
  public static OrderItemEvent of(UUID productId, int quantity) {
    return new OrderItemEvent(productId, quantity);
  }
}

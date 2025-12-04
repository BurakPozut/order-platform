package com.burakpozut.order_service.app.command;

public record UpdateOrderItemCommand(
    OrderItemData item) {

  public static UpdateOrderItemCommand of(
      OrderItemData item) {
    return new UpdateOrderItemCommand(item);
  }
}

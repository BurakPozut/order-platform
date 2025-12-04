package com.burakpozut.order_service.api.dto.request;

import com.burakpozut.order_service.app.command.OrderItemData;
import com.burakpozut.order_service.app.command.UpdateOrderItemCommand;

public record UpdateOrderItemRequest(
    OrderItemRequest item) {
  public static UpdateOrderItemCommand toCommand(UpdateOrderItemRequest request) {
    var orderItemData = new OrderItemData(request.item.productId(), request.item.quantity());
    return new UpdateOrderItemCommand(orderItemData);
  }
}

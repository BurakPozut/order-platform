package com.burakpozut.order_service.api;

import com.burakpozut.order_service.api.dto.request.CreateOrderRequest;
import com.burakpozut.order_service.api.dto.request.UpdateOrderItemRequest;
import com.burakpozut.order_service.api.dto.request.UpdateOrderRequest;
import com.burakpozut.order_service.app.command.CreateOrderCommand;
import com.burakpozut.order_service.app.command.OrderItemData;
import com.burakpozut.order_service.app.command.UpdateOrderCommand;
import com.burakpozut.order_service.app.command.UpdateOrderItemCommand;

public class OrderMapper {
  public static UpdateOrderCommand toCommand(UpdateOrderRequest request) {
    return UpdateOrderCommand.of(
        request.customerId(),
        request.status(),
        request.currency());
  }

  public static CreateOrderCommand toCommand(CreateOrderRequest request) {
    var items = request.items().stream()
        .map(item -> new OrderItemData(
            item.productId(), item.quantity()))
        .toList();
    return CreateOrderCommand.of(
        request.customerId(),
        request.status(),
        request.currency(), items);
  }

  public static UpdateOrderItemCommand toCommand(UpdateOrderItemRequest request) {
    var orderItemData = new OrderItemData(
        request.item().productId(),
        request.item().quantity());

    return UpdateOrderItemCommand.of(orderItemData);
  }

}

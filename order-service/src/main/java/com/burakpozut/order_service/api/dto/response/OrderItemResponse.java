package com.burakpozut.order_service.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import com.burakpozut.order_service.domain.OrderItem;

public record OrderItemResponse(
    UUID id,
    UUID productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity) {
  public static OrderItemResponse from(OrderItem item) {
    return new OrderItemResponse(
        item.id(),
        item.productId(),
        item.productName(),
        item.unitPrice(),
        item.quantity());
  }
}

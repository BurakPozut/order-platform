package com.burakpozut.order_service.app.command;

import java.util.UUID;

public record OrderItemData(
    UUID productId,
    Integer quantity) {
}
package com.burakpozut.microservices.order_platform_monolith.order.api.dto;

import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.order.domain.Order;
import com.burakpozut.microservices.order_platform_monolith.order.domain.OrderStatus;

public record OrderResponse(
        UUID id,
        UUID customerId,
        OrderStatus status) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getOrderStatus());
    }
}

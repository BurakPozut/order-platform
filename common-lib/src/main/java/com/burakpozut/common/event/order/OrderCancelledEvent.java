package com.burakpozut.common.event.order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID eventId,
        Instant occurredAt,
        UUID orderId,
        UUID customerId,
        List<OrderItemEvent> items,
        String reason) implements OrderEvent {

    public static OrderCancelledEvent of(UUID eventId, Instant occurredAt,
            UUID orderId, UUID customerId, List<OrderItemEvent> items, String reason) {
        return new OrderCancelledEvent(eventId, occurredAt, orderId, customerId, items, reason);
    }
}

package com.burakpozut.common.event.order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCompensationEvent(
        UUID eventId,
        Instant occurredAt,
        UUID orderId,
        UUID customerId,
        List<OrderItemEvent> items,
        String reason,
        String groupId) implements OrderEvent {
    public static OrderCompensationEvent of(UUID eventId,
            Instant occurredAt,
            UUID orderId, UUID customerId,
            List<OrderItemEvent> items,
            String reason,
            String groupId) {
        return new OrderCompensationEvent(eventId, occurredAt,
                orderId, customerId, items, reason, groupId);
    }
}

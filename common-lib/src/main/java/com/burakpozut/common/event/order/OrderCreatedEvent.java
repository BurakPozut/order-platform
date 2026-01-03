package com.burakpozut.common.event.order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
                UUID eventId,
                Instant occurredAt,
                UUID orderId,
                UUID customerId,
                List<OrderItemEvent> items) implements OrderEvent {
        public static OrderCreatedEvent of(UUID eventId, Instant occurredAt, UUID orderId, UUID customerId,
                        List<OrderItemEvent> items) {
                return new OrderCreatedEvent(eventId, occurredAt, orderId, customerId, items);
        }

}

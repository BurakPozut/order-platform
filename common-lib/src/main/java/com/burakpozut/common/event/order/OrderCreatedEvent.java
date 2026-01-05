package com.burakpozut.common.event.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;

public record OrderCreatedEvent(
                UUID eventId,
                Instant occurredAt,
                UUID orderId,
                UUID customerId,
                BigDecimal totalAmount,
                Currency currency,
                List<OrderItemEvent> items) implements OrderEvent {
        public static OrderCreatedEvent of(UUID eventId, Instant occurredAt, UUID orderId,
                        UUID customerId, BigDecimal totalAmount,
                        Currency currency,
                        List<OrderItemEvent> items) {
                return new OrderCreatedEvent(eventId, occurredAt,
                                orderId, customerId,
                                totalAmount, currency, items);
        }

}

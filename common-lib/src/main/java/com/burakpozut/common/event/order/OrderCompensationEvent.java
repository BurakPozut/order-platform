package com.burakpozut.common.event.order;

import java.time.Instant;
import java.util.UUID;

public record OrderCompensationEvent(
    UUID eventId,
    Instant occurredAt,
    UUID orderId,
    UUID customerId,
    String reason) implements OrderEvent {
}

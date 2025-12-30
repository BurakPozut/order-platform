package com.burakpozut.common.event.order;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID orderId,
    UUID customerId) implements OrderEvent {

}

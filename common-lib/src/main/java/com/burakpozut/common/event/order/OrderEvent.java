package com.burakpozut.common.event.order;

import java.time.Instant;
import java.util.UUID;

public sealed interface OrderEvent permits OrderCreatedEvent, OrderCompensationEvent {
    UUID eventId();

    Instant occurredAt();

    UUID orderId();
}

package com.burakpozut.common.event.order;

import java.time.Instant;
import java.util.UUID;

public interface OrderEvent {
    UUID eventId();

    Instant occurredAt();

    UUID orderId();

    UUID customerId();
}

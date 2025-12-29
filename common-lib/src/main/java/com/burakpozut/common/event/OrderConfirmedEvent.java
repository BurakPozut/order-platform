package com.burakpozut.common.event;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
    UUID eventId,
    Instant occuredAt,
    UUID orderId,
    UUID customerId) {

}

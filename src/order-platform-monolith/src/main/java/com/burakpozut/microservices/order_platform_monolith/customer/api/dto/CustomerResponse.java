package com.burakpozut.microservices.order_platform_monolith.customer.api.dto;

import java.util.UUID;

public record CustomerResponse(
    UUID id,
    String fullName,
    String email) {
}

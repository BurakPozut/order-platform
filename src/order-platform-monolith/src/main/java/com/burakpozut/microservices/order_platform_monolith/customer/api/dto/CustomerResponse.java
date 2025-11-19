package com.burakpozut.microservices.order_platform_monolith.customer.api.dto;

import java.util.UUID;

import org.springframework.lang.NonNull;

public record CustomerResponse(
    @NonNull UUID id,
    @NonNull String fullName,
    @NonNull String email) {
}

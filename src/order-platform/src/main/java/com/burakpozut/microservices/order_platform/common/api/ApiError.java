package com.burakpozut.microservices.order_platform.common.api;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message) {
}
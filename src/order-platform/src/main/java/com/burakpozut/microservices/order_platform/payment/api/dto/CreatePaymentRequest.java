package com.burakpozut.microservices.order_platform.payment.api.dto;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.payment.domain.PaymentStatus;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull UUID orderId,
        PaymentStatus status,
        @NotNull String provider,
        @NotNull String providerRef) {

}

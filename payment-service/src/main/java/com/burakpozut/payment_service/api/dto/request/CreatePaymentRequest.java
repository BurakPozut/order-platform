package com.burakpozut.payment_service.api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.payment_service.domain.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequest(
        @NotNull UUID orderId,
        @NotNull @Positive BigDecimal amount, // TODO: amount should be fetched from the order or calculated
        @NotNull Currency currency,
        @NotNull PaymentStatus status,
        @NotNull String provider,
        @NotNull String providerRef) {

}

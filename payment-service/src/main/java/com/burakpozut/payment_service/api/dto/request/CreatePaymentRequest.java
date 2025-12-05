package com.burakpozut.payment_service.api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequest(
                @NotNull UUID orderId,
                @NotNull @Positive BigDecimal amount,
                @NotNull Currency currency,
                @NotNull String provider,
                @NotNull String providerRef) {

}

package com.burakpozut.payment_service.api.dto.request;

import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.payment_service.domain.PaymentStatus;

import jakarta.annotation.Nullable;

public record PatchPaymentRequest(
    @Nullable UUID orderId,
    @Nullable Currency currency,
    @Nullable PaymentStatus status,
    @Nullable String provider,
    @Nullable String providerRef) {

}

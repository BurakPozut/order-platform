package com.burakpozut.microservices.order_platform.payment.application.command;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.payment.domain.PaymentStatus;

public record CreatePaymentCommand(
        UUID orderId,
        PaymentStatus status,
        String provider,
        String provideRef) {

}

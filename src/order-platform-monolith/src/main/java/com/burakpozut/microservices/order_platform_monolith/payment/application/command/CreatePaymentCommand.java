package com.burakpozut.microservices.order_platform_monolith.payment.application.command;

import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.payment.domain.PaymentStatus;

public record CreatePaymentCommand(
    UUID orderId,
    PaymentStatus status,
    String provider,
    String provideRef) {

}

package com.burakpozut.payment_service.api;

import com.burakpozut.payment_service.api.dto.request.CreatePaymentRequest;
import com.burakpozut.payment_service.api.dto.request.PatchPaymentRequest;
import com.burakpozut.payment_service.app.command.CreatePaymentCommand;
import com.burakpozut.payment_service.app.command.PatchPaymentCommand;
import com.burakpozut.payment_service.domain.PaymentStatus;

public class PaymentMapper {
  public static CreatePaymentCommand toCommand(CreatePaymentRequest request) {
    return CreatePaymentCommand.of(request.orderId(), request.amount(),
        request.currency(), PaymentStatus.PENDING,
        request.provider(), request.providerRef());
  }

  public static PatchPaymentCommand toCommand(PatchPaymentRequest request) {
    return PatchPaymentCommand.of(request.orderId(), request.currency(),
        request.status(), request.provider(), request.providerRef());
  }
}
package com.burakpozut.payment_service.api;

import com.burakpozut.payment_service.api.dto.request.CreatePaymentRequest;
import com.burakpozut.payment_service.app.command.CreatePaymentCommand;

public class PaymentMapper {
  public static CreatePaymentCommand toCommand(CreatePaymentRequest request) {
    return CreatePaymentCommand.of(request.orderId(), request.amount(),
        request.currency(), request.status(),
        request.provider(), request.providerRef());
  }

}

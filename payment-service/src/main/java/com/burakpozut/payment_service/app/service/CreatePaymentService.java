package com.burakpozut.payment_service.app.service;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.app.command.CreatePaymentCommand;
import com.burakpozut.payment_service.app.exception.OrderNotFoundException;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;
import com.burakpozut.payment_service.domain.gateway.OrderGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePaymentService {
  private final PaymentRepository paymentRepository;
  private final OrderGateway orderGateway;

  public Payment handle(CreatePaymentCommand command) {
    // Check for if the order is present
    if (!orderGateway.validateOrderId(command.orderId())) {
      throw new OrderNotFoundException(command.orderId());
    }
    var payment = Payment.of(command.orderId(), command.amount(),
        command.currency(), command.status(), command.provider(), command.providerRef());
    return paymentRepository.save(payment, true);
  }

}
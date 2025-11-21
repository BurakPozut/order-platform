package com.burakpozut.microservices.order_platform.payment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.order.application.exception.OrderNotFoundException;
import com.burakpozut.microservices.order_platform.payment.application.command.CreatePaymentCommand;
import com.burakpozut.microservices.order_platform.payment.domain.Payment;
import com.burakpozut.microservices.order_platform.payment.domain.PaymentRepository;
import com.burakpozut.microservices.order_platform.payment.domain.port.OrderGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePaymentService {
  private final PaymentRepository paymentRepository;
  private final OrderGateway orderGateway;

  @Transactional
  public Payment handle(CreatePaymentCommand command) {
    var orderAmount = orderGateway.getOrderAmount(command.orderId())
        .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

    var payment = Payment.createNew(command.orderId(), orderAmount, command.status(), command.provider(),
        command.provideRef());
    return paymentRepository.save(payment);
  }

}

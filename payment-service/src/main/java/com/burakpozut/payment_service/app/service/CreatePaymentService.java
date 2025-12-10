package com.burakpozut.payment_service.app.service;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.app.command.CreatePaymentCommand;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePaymentService {
  private final PaymentRepository paymentRepository;

  @Transactional
  public Payment handle(CreatePaymentCommand command) {
    // !! Right now we are not checking order cause the order service calls this
    // and it also depends on this so this create distrubated transaction issue
    // This method is only called by the order service by logic and desing

    // if (!orderGateway.validateOrderId(command.orderId())) {
    // throw new OrderNotFoundException(command.orderId());
    // }

    // String idempotencyKey = deriveKey(command.orderId(), command.amount(),
    // command.providerRef());

    // // Optional<Payment> exsiting =
    // paymentRepository.findByIdempotencyKey(idempotencyKey);
    // // if (exsiting.isPresent()) {
    // // return exsiting.get();
    // }

    var payment = Payment.of(command.orderId(), command.amount(),
        command.currency(), command.status(),
        command.provider(), command.providerRef());

    return paymentRepository.save(payment, true);
  }
}
package com.burakpozut.payment_service.app.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.app.command.CreatePaymentCommand;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePaymentService {
  private final int BUCKET_MINUTES = 5;

  private final PaymentRepository paymentRepository;

  public Payment handle(CreatePaymentCommand command) {
    // !! Right now we are not checking order cause the order service calls this
    // and it also depends on this so this create distrubated transaction issue
    // This method is only called by the order service by logic and desing

    // if (!orderGateway.validateOrderId(command.orderId())) {
    // throw new OrderNotFoundException(command.orderId());
    // }

    String idempotencyKey = deriveKey(command.orderId(), command.amount(), command.providerRef());

    Optional<Payment> exsiting = paymentRepository.findByIdempotencyKey(idempotencyKey);
    if (exsiting.isPresent()) {
      return exsiting.get();
    }

    var payment = Payment.of(command.orderId(), command.amount(),
        command.currency(), command.status(), command.provider(), command.providerRef());

    return paymentRepository.save(payment, true);
  }

  private String deriveKey(UUID orderId, BigDecimal amount, String providerRef) {
    Instant now = Instant.now();
    long buckerStart = now.truncatedTo(ChronoUnit.MINUTES)
        .minus(now.getEpochSecond() / 60 % BUCKET_MINUTES, ChronoUnit.MINUTES)
        .getEpochSecond();

    return orderId + ":" + amount + ":" + providerRef + ":" + buckerStart;
  }
}
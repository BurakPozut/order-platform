package com.burakpozut.payment_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.app.command.PatchPaymentCommand;
import com.burakpozut.payment_service.app.exception.PaymentNotFoundException;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatchPaymentService {
  private final PaymentRepository paymentRepository;

  public Payment handle(UUID paymentId, PatchPaymentCommand command) {
    var existing = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new PaymentNotFoundException(paymentId));

    UUID orderId = command.orderId() != null ? command.orderId() : existing.orderId();
    var currency = command.currency() != null ? command.currency() : existing.currency();
    var status = command.status() != null ? command.status() : existing.status();
    var provider = command.provider() != null ? command.provider() : existing.provider();
    var providerRef = command.providerRef() != null ? command.providerRef() : existing.providerRef();

    var updated = Payment.rehydrate(paymentId, orderId,
        existing.amount(), currency,
        status, provider, providerRef, existing.idempotencyKey(), existing.updatedAt());
    return paymentRepository.save(updated, false);
  }
}

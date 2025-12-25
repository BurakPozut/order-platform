package com.burakpozut.payment_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.app.command.PatchPaymentCommand;
import com.burakpozut.payment_service.app.exception.PaymentNotFoundException;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatchPaymentService {
  private final PaymentRepository paymentRepository;

  public Payment handle(UUID paymentId, PatchPaymentCommand command) {
    var existing = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new PaymentNotFoundException(paymentId));

    var updated = existing.update(
        command.status(),
        command.currency(),
        command.provider(),
        command.providerRef());

    if (updated != existing) {
      if (command.status() != null && !command.status().equals(existing.status())) {
        log.info("Payment {} status transition: {} -> {}",
            paymentId, existing.status(), command.status());
      }
      return paymentRepository.save(updated, false);
    }
    return existing;
  }
}
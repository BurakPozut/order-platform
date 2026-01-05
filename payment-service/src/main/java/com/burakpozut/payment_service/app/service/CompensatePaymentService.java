package com.burakpozut.payment_service.app.service;

import java.util.UUID;

import com.burakpozut.payment_service.domain.PaymentRepository;
import com.burakpozut.payment_service.domain.PaymentStatus;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompensatePaymentService {

  private final PaymentRepository paymentRepository;

  @Transactional
  public void handle(UUID orderId, String reason) {
    var paymentOpt = paymentRepository.findByOrderId(orderId);

    if (paymentOpt.isEmpty()) {
      log.warn("Payment not found for order: {}. Compensation skipped. Reason:{}",
          orderId, reason);
      return;
    }

    var payment = paymentOpt.get();
    PaymentStatus targetStatus = determineCompensationStatus(payment.status());

    if (targetStatus == null) {
      log.info("Payment {} for order {} is already in terminal state: {}. No compensation needed.",
          payment.id(), orderId, payment.status());
      return;
    }

    log.info("Compensating payment {} for order {}. Status transition: {} -> {}. Reason: {}",
        payment.id(), orderId, payment.status(), targetStatus, reason);

    var compensated = payment.update(targetStatus, null, null, null);
    paymentRepository.save(compensated, false);

    log.info("Successgully compensated payment {} for order {}. New status: {}",
        payment.id(), orderId, targetStatus);

  }

  private PaymentStatus determineCompensationStatus(PaymentStatus currenStatus) {
    return switch (currenStatus) {
      case PENDING -> PaymentStatus.CANCELLED;
      case COMPLETED -> PaymentStatus.REFUNDED;
      case FAILED, CANCELLED, REFUNDED -> null;
    };
  }
}
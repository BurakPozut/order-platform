package com.burakpozut.payment_service.app.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.payment_service.app.command.CreatePaymentCommand;
import com.burakpozut.payment_service.app.exception.OrderNotFoundException;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;
import com.burakpozut.payment_service.domain.gateway.OrderGateway;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentService {
  private static final int BUCKET_MINUTES = 5;

  private final PaymentRepository paymentRepository;
  private final OrderGateway orderGateway;

  @Transactional
  public Payment handle(CreatePaymentCommand command) {
    long currentBucket = System.currentTimeMillis() / (1000 * 60 * BUCKET_MINUTES);

    String currentKey = deriveKey(command.orderId(), command.amount(), currentBucket);
    Optional<Payment> existing = paymentRepository.findByIdempotencyKey(currentKey);

    if (existing.isPresent()) {
      log.info("Returning existing order from current bucket: {}", currentKey);
      return existing.get();
    }

    String prevKey = deriveKey(command.orderId(), command.amount(), currentBucket - 1);
    Optional<Payment> existingPrev = paymentRepository.findByIdempotencyKey(prevKey);

    if (existingPrev.isPresent() && isRecent(existingPrev.get())) {
      log.info("Returning existing order from previous bucket : {}", prevKey);
      return existingPrev.get();
    }

    validateOrderExists(command.orderId());

    var payment = Payment.of(command.orderId(), command.amount(),
        command.currency(), command.status(),
        command.provider(), command.providerRef(), currentKey);

    return paymentRepository.save(payment, true);
  }

  private String deriveKey(UUID orderId, BigDecimal amount, long bucket) {
    return orderId + "::" + amount + "::" + bucket;
  }

  private boolean isRecent(Payment payment) {
    LocalDateTime deadline = LocalDateTime.now().minusMinutes(BUCKET_MINUTES);
    return payment.updatedAt().isAfter(deadline);
  }

  private void validateOrderExists(UUID orderId) {
    try {
      orderGateway.validateOrderId(orderId);
    } catch (ExternalServiceNotFoundException e) {
      throw new OrderNotFoundException(orderId);
    }
  }
}

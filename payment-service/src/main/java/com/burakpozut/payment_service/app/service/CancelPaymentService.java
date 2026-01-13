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
public class CancelPaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void handle(UUID orderId) {
        var paymentOpt = paymentRepository.findById(orderId);
        if (paymentOpt.isEmpty()) {
            log.warn("Payment not found for order: {}. Cancellation skipped.", orderId);
            return;
        }

        var payment = paymentOpt.get();
        PaymentStatus targetStatus = determineCancellationStatus(payment.status());

        if (targetStatus == null) {
            log.info("Payment {} for order {} is already in terminal state: {}. No cancellation needed.",
                    payment.id(), orderId, payment.status());
            return;
        }

        log.info("CAncelling payment {} for order {}. Status transtition: {} -> {}.",
                payment.id(), orderId, payment.status(), targetStatus);

        var cancelled = payment.update(targetStatus, null, null, null);
        paymentRepository.save(cancelled, false);

        log.info("Successfully cancelled payment {} for order {}. New status: {}",
                payment.id(), orderId, targetStatus);
    }

    private PaymentStatus determineCancellationStatus(PaymentStatus currentStatus) {
        return switch (currentStatus) {
            case PENDING -> PaymentStatus.CANCELLED;
            case COMPLETED -> PaymentStatus.REFUNDED;
            case FAILED, CANCELLED, REFUNDED -> null;
        };
    }
}

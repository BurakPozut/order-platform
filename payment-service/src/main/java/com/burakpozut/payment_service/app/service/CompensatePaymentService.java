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

        log.info("payment.compensate.start orderId={} reason={}", orderId, reason);

        if (paymentOpt.isEmpty()) {
            log.warn("payment.compensate.not_found orderId={} reason={} action=skipping",
                    orderId, reason);
            return;
        }

        var payment = paymentOpt.get();
        PaymentStatus targetStatus = determineCompensationStatus(payment.status());

        if (targetStatus == null) {
            log.info("payment.compensate.already_terminal paymentId={} orderId={} status={} action=skipping",
                    payment.id(), orderId, payment.status());
            return;
        }

        log.info("payment.compensate.transition paymentId={} orderId={} fromStatus={} toStatus={} reason={}",
                payment.id(), orderId, payment.status(), targetStatus, reason);

        var compensated = payment.update(targetStatus, null, null, null);
        paymentRepository.save(compensated, false);

        log.info("payment.compensate.completed paymentId={} orderId={} newStatus={}",
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
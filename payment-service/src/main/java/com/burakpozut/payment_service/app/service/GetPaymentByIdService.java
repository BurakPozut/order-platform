package com.burakpozut.payment_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.app.exception.PaymentNotFoundException;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPaymentByIdService {
  private final PaymentRepository paymentRepository;

  public Payment handle(UUID id) {
    log.debug("payment.getById.start paymentId={}", id);
    var payment = paymentRepository.findById(id)
        .orElseThrow(() -> new PaymentNotFoundException(id));
    log.info("payment.getById.found paymentId={} orderId={} status={}",
            payment.id(), payment.orderId(), payment.status());
    return payment;
  }

}

package com.burakpozut.payment_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.app.exception.PaymentNotFoundException;
import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetPaymentByIdService {
  private final PaymentRepository paymentRepository;

  public Payment handle(UUID id) {
    var payment = paymentRepository.findById(id)
        .orElseThrow(() -> new PaymentNotFoundException(id));
    return payment;
  }

}

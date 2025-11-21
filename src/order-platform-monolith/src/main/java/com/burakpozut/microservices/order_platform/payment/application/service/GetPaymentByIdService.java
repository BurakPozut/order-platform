package com.burakpozut.microservices.order_platform.payment.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform.payment.application.exception.PaymentNotFound;
import com.burakpozut.microservices.order_platform.payment.domain.Payment;
import com.burakpozut.microservices.order_platform.payment.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetPaymentByIdService {
  private final PaymentRepository paymentRepository;

  public Payment handle(UUID id) {
    return paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFound(id));
  }

}

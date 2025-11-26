package com.burakpozut.microservices.order_platform.payment.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.payment.application.exception.PaymentNotFound;
import com.burakpozut.microservices.order_platform.payment.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeletePaymentService {
  private final PaymentRepository paymentRepository;

  @Transactional
  public void handle(UUID id) {
    if (!paymentRepository.findById(id).isPresent())
      throw new PaymentNotFound(id);
    paymentRepository.deleteById(id);
  }

}

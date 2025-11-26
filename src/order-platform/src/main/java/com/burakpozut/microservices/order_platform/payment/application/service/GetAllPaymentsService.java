package com.burakpozut.microservices.order_platform.payment.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform.payment.domain.Payment;
import com.burakpozut.microservices.order_platform.payment.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllPaymentsService {
  private final PaymentRepository paymentRepository;

  public List<Payment> handle() {
    return paymentRepository.findAll();
  }

}

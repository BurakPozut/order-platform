package com.burakpozut.payment_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllPaymentsService {
  private final PaymentRepository paymentRepository;

  public List<Payment> handle() {
    return paymentRepository.findAll();
  }

}

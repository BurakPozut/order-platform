package com.burakpozut.payment_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllPaymentsService {
  private final PaymentRepository paymentRepository;

  public List<Payment> handle() {
    log.info("payment.getAll.start");
    var payments = paymentRepository.findAll();
    log.info("payment.getAll.completed count={}", payments.size());
    return payments;
  }

}

package com.burakpozut.microservices.order_platform.payment.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform.payment.domain.Payment;
import com.burakpozut.microservices.order_platform.payment.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositroyAdapter implements PaymentRepository {
  private final SpringDataPaymentRepository jpa;

  @Override
  public Optional<Payment> findById(UUID id) {
    return jpa.findById(id).map(PaymentMapper::toDomain);
  }

  @Override
  public List<Payment> findAll() {
    return jpa.findAll().stream().map(PaymentMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Payment save(Payment payment) {
    var entity = PaymentMapper.toEntity(payment);
    var savedEntity = jpa.save(entity);

    return PaymentMapper.toDomain(savedEntity);
  }
}

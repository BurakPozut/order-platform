package com.burakpozut.payment_service.infra.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.burakpozut.payment_service.domain.Payment;
import com.burakpozut.payment_service.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRespositoryAdapter implements PaymentRepository {
  private final SpringDataPaymentRespository jpa;

  @Override
  public Optional<Payment> findById(UUID id) {
    return jpa.findById(id).map(PaymentMapper::toDomain);
  }

  @Override
  public List<Payment> findAll() {
    return jpa.findAll().stream().map(PaymentMapper::toDomain).toList();
  }

  @Override
  public Payment save(Payment p, boolean isNew) {
    var entity = PaymentMapper.toEntity(p, isNew);
    var saved = jpa.save(entity);
    return PaymentMapper.toDomain(saved);
  }

  @Override
  public void deleteById(UUID id) {
    // TODO Auto-generated method stub

  }
}

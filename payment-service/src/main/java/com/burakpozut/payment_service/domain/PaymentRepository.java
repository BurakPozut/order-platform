package com.burakpozut.payment_service.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
  Optional<Payment> findById(UUID id);

  List<Payment> findAll();

  Payment save(Payment p, boolean isNew);

  void deleteById(UUID id);

  Optional<Payment> findByIdempotencyKey(String idempotencyKey);

  Optional<Payment> findByOrderId(UUID orderId);
}

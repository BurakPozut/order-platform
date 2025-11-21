package com.burakpozut.microservices.order_platform.payment.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
  Optional<Payment> findById(UUID id);

  List<Payment> findAll();

  Payment save(Payment payment);
}

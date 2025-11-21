package com.burakpozut.microservices.order_platform_monolith.payment.infrastructure.persistence;

import com.burakpozut.microservices.order_platform_monolith.payment.domain.Payment;

public class PaymentMapper {
  static Payment toDomain(PaymentJpaEntiry entity) {
    return Payment.rehydrate(entity.getId(), entity.getOrderId(), entity.getAmount(), entity.getStatus(),
        entity.getProvider(),
        entity.getProviderRef());
  }

  static PaymentJpaEntiry toEntity(Payment p) {
    var entity = new PaymentJpaEntiry();
    entity.setId(p.getId());
    entity.setOrderId(p.getOrderId());
    entity.setAmount(p.getAmount());
    entity.setStatus(p.getStatus());
    entity.setProvider(p.getProvider());
    entity.setProviderRef(p.getProviderRef());
    return entity;
  }
}

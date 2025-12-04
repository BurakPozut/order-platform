package com.burakpozut.payment_service.infra.persistance;

import com.burakpozut.payment_service.domain.Payment;

public class PaymentMapper {
  public static Payment toDomain(PaymentJpaEntity entity) {
    return Payment.rehydrate(
        entity.getId(),
        entity.getOrderId(),
        entity.getAmount(),
        entity.getCurrency(),
        entity.getStatus(),
        entity.getProvider(),
        entity.getProviderRef(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public static PaymentJpaEntity toEntity(Payment payment, boolean isNew) {
    PaymentJpaEntity entity = new PaymentJpaEntity();
    entity.setId(payment.id());
    entity.setOrderId(payment.orderId());
    entity.setAmount(payment.amount());
    entity.setCurrency(payment.currency());
    entity.setStatus(payment.status());
    entity.setProvider(payment.provider());
    entity.setProviderRef(payment.providerRef());
    entity.setCreatedAt(payment.createdAt());
    entity.setUpdatedAt(payment.updatedAt());
    entity.setNew(isNew);
    return entity;
  }

}

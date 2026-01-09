package com.burakpozut.order_service.infra.persistance;

import com.burakpozut.order_service.domain.OrderConfirmationState;

public class OrderConfirmationStateMapper {

    public static OrderConfirmationState toDomain(OrderConfirmationStateJpaEntity entity) {
        return OrderConfirmationState.rehydrate(
                entity.getId(),
                entity.getOrderId(),
                entity.getPaymentCompleted(),
                entity.getProductCompleted(),
                entity.getNotificationCompleted(),
                entity.getConfirmedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static OrderConfirmationStateJpaEntity toEntity(OrderConfirmationState domain, boolean isNew) {
        var entity = new OrderConfirmationStateJpaEntity();
        entity.setId(domain.id());
        entity.setOrderId(domain.orderId());
        entity.setPaymentCompleted(domain.paymentCompleted());
        entity.setProductCompleted(domain.productCompleted());
        entity.setNotificationCompleted(domain.notificationCompleted());
        entity.setConfirmedAt(domain.confirmedAt());
        // createdAt and updatedAt are handled by @EntityListeners
        return entity;
    }
}

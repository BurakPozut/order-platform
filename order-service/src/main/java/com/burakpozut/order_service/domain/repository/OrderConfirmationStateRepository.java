package com.burakpozut.order_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.burakpozut.order_service.domain.OrderConfirmationState;

public interface OrderConfirmationStateRepository {
    Optional<OrderConfirmationState> findByOrderId(UUID orderId);

    OrderConfirmationState save(OrderConfirmationState state, boolean isNew);

    void deleteByOrderId(UUID orderId);

}

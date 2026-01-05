package com.burakpozut.order_service.domain;

import java.util.Optional;
import java.util.UUID;

public interface OrderConfirmationStateRepository {
    Optional<OrderConfirmationState> findByOrderId(UUID orderId);

    OrderConfirmationState save(OrderConfirmationState state, boolean isNew);

    void deleteByOrderId(UUID orderId);

}

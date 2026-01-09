package com.burakpozut.order_service.infra.persistance;

import java.util.Optional;
import java.util.UUID;

import com.burakpozut.order_service.domain.OrderConfirmationState;
import com.burakpozut.order_service.domain.repository.OrderConfirmationStateRepository;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderConfirmationStateRepositoryAdapter implements OrderConfirmationStateRepository {
    private final SpringDataOrderConfirmationStateRepository jpa;

    @Override
    public Optional<OrderConfirmationState> findByOrderId(UUID orderId) {
        return jpa.findByOrderId(orderId).map(OrderConfirmationStateMapper::toDomain);
    }

    @Override
    public OrderConfirmationState save(OrderConfirmationState state, boolean isNew) {
        var entity = OrderConfirmationStateMapper.toEntity(state, isNew);
        var savedEntity = jpa.save(entity);

        return OrderConfirmationStateMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteByOrderId(UUID orderId) {
        jpa.deleteByOrderId(orderId);
    }
}

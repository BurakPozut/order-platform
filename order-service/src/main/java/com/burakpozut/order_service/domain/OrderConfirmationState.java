package com.burakpozut.order_service.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.burakpozut.common.domain.ServiceName;

public record OrderConfirmationState(
        UUID id,
        UUID orderId,
        boolean paymentCompleted,
        boolean productCompleted,
        boolean notificationCompleted,
        LocalDateTime confirmedAt,
        LocalDateTime createdAt,
        LocalDateTime updateAt) {

    public boolean isAllCompleted() {
        return paymentCompleted && productCompleted && notificationCompleted;
    }

    public static OrderConfirmationState createFor(UUID orderId) {
        return new OrderConfirmationState(UUID.randomUUID(), orderId,
                false, false, false, null, LocalDateTime.now(),
                LocalDateTime.now());
    }

    public OrderConfirmationState markServiceCompleted(ServiceName serviceName) {
        return switch (serviceName) {
            case PAYMENT -> new OrderConfirmationState(
                    id, orderId, true, productCompleted, notificationCompleted,
                    confirmedAt, createdAt, LocalDateTime.now());
            case PRODUCT -> new OrderConfirmationState(
                    id, orderId, paymentCompleted, true, notificationCompleted,
                    confirmedAt, createdAt, LocalDateTime.now());
            case NOTIFICATION -> new OrderConfirmationState(
                    id, orderId, paymentCompleted, productCompleted, true,
                    confirmedAt, createdAt, LocalDateTime.now());
            default -> throw new IllegalArgumentException("Unknown service: " + serviceName);
        };
    }

    public OrderConfirmationState markConfirmed() {
        return new OrderConfirmationState(
                id, orderId, paymentCompleted, productCompleted, notificationCompleted,
                LocalDateTime.now(), createdAt, LocalDateTime.now());
    }

    public static OrderConfirmationState rehydrate(
            UUID id,
            UUID orderId,
            boolean paymentCompleted,
            boolean productCompleted,
            boolean notificationCompleted,
            LocalDateTime confirmedAt,
            LocalDateTime createdAt,
            LocalDateTime updateAt) {
        return new OrderConfirmationState(
                id, orderId, paymentCompleted, productCompleted, notificationCompleted,
                confirmedAt, createdAt, updateAt);
    }

    public static OrderConfirmationState of(
            UUID id,
            UUID orderId,
            boolean paymentCompleted,
            boolean productCompleted,
            boolean notificationCompleted,
            LocalDateTime confirmedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new OrderConfirmationState(
                id, orderId, paymentCompleted, productCompleted, notificationCompleted,
                confirmedAt, createdAt, updatedAt);
    }

}

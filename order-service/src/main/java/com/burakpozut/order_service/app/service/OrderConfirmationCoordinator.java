package com.burakpozut.order_service.app.service;

import java.util.UUID;

import com.burakpozut.common.event.order.ServiceCompletionEvent;
import com.burakpozut.order_service.app.command.UpdateOrderCommand;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderConfirmationState;
import com.burakpozut.order_service.domain.OrderConfirmationStateRepository;
import com.burakpozut.order_service.domain.OrderRepository;
import com.burakpozut.order_service.domain.OrderStatus;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmationCoordinator {
    private final OrderConfirmationStateRepository confirmationStateRepository;
    private final OrderRepository orderRepository;
    private final UpdateOrderService updateOrderService;

    @KafkaListener(topics = "${app.kafka.topics.service-completions}", groupId = "${spring.kafka.consumer.group-id}")

    @Transactional
    public void handleServiceCompletion(ServiceCompletionEvent event) {
        UUID orderId = event.orderId();
        String serviceName = event.serviceName();

        log.info("Received completion event for order: {} from service: {}",
                orderId, serviceName);

        OrderConfirmationState state = confirmationStateRepository
                .findByOrderId(orderId)
                .orElseGet(() -> {
                    log.info("Creating new confirmation state for order: {}", orderId);
                    return confirmationStateRepository.save(
                            OrderConfirmationState.createFor(orderId), true);
                });

        OrderConfirmationState updateState = state.markServiceCompleted(serviceName);
        confirmationStateRepository.save(updateState, false);

        log.info("Service {} completed for order: {}, State: payment = {}, product={}, notification={}",
                serviceName, orderId,
                updateState.paymentCompleted(),
                updateState.productCompleted(),
                updateState.notificationCompleted());

        if (updateState.isAllCompleted() && updateState.confirmedAt() == null) {
            confirmOrder(orderId, updateState);
        }
    }

    private void confirmOrder(UUID orderId, OrderConfirmationState state) {
        log.info("All services completed for order: {}. Confirming order...", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));

        if (order.status() == OrderStatus.PENDING) {
            UpdateOrderCommand command = UpdateOrderCommand.of(null, OrderStatus.CONFIRMED, null);
            updateOrderService.handle(orderId, command);

            OrderConfirmationState confirmedState = state.markConfirmed();
            confirmationStateRepository.save(confirmedState, false);

            log.info("Order {} confirmed successfully", orderId);
            // TODO: publish orderConfirmed event to update payment
        } else {
            log.warn("Order {} is not in PENDING status. Current status {}",
                    orderId, order.status());
        }
        // TODO: We might need to change something in the compensation event to make the
        // order confirmation state to be cancelled
    }

}

package com.burakpozut.order_service.app.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.common.event.order.OrderItemEvent;
import com.burakpozut.order_service.app.exception.OrderNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderStatus;
import com.burakpozut.order_service.domain.repository.OrderRepository;
import com.burakpozut.order_service.infra.kafka.OrderCancelledPublisher;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelOrderService {

    private final OrderRepository orderRepository;
    private final OrderCancelledPublisher orderCancelledPublisher;

    @Transactional
    public void handle(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        log.info("order.cancel.start orderId={}", orderId);

        Order cancelledOrder = Order.rehydrate(orderId, order.customerId(),
                OrderStatus.CANCELLED, order.totalAmount(),
                order.currency(), order.items(),
                order.idempotencyKey(), null);

        orderRepository.save(cancelledOrder, false);
        if (order.status() == OrderStatus.CANCELLED) {
            log.info("order.cancel.already_cancelled orderId={} action=skipping", orderId);
            return;
        }
        List<OrderItemEvent> items = order.items().stream()
                .map(item -> OrderItemEvent.of(item.productId(), item.quantity())).toList();
        orderCancelledPublisher.publish(orderId, order.customerId(), items, null);
    }
}

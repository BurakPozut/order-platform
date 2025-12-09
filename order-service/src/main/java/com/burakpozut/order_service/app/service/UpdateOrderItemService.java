package com.burakpozut.order_service.app.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.app.command.UpdateOrderItemCommand;
import com.burakpozut.order_service.app.exception.OrderItemNotFoundException;
import com.burakpozut.order_service.app.exception.OrderNotFoundException;
import com.burakpozut.order_service.app.exception.OrderStatusException;
import com.burakpozut.order_service.app.exception.product.ProductNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderItem;
import com.burakpozut.order_service.domain.OrderRepository;
import com.burakpozut.order_service.domain.OrderStatus;
import com.burakpozut.order_service.domain.gateway.ProductGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateOrderItemService {
    private final OrderRepository orderRepository;
    private final ProductGateway productGateway;

    public Order handle(UUID orderId, UUID orderItemId, UpdateOrderItemCommand command) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.status().equals(OrderStatus.PENDING))
            throw new OrderStatusException("Order status must be PENDING");

        var newItem = command.item();
        var newItemProduct = productGateway.getProductById(newItem.productId())
                .orElseThrow(() -> new ProductNotFoundException(newItem.productId()));

        var oldOrderItem = order.items().stream()
                .filter(item -> item.id().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderItemNotFoundException(orderItemId));

        // Calculate old item total
        BigDecimal oldItemTotal = oldOrderItem.unitPrice()
                .multiply(BigDecimal.valueOf(oldOrderItem.quantity()));

        // Calculate new item total
        BigDecimal newItemTotal = newItemProduct.price()
                .multiply(BigDecimal.valueOf(newItem.quantity()));

        // Subtract old amount, add new amount
        var newTotalAmount = order.totalAmount()
                .subtract(oldItemTotal)
                .add(newItemTotal);

        var updatedItem = OrderItem.rehydrate(oldOrderItem.id(),
                newItemProduct.productId(), newItemProduct.name(),
                newItemProduct.price(), newItem.quantity());

        var updatedItems = order.items().stream()
                .map(item -> item.id().equals(orderItemId) ? updatedItem : item)
                .toList();

        var updatedOrder = Order.rehydrate(order.id(), order.customerId(),
                order.status(), newTotalAmount, order.currency(),
                updatedItems, order.idempotencyKey(), order.updatedAt());

        return orderRepository.save(updatedOrder, false);

    }

}
